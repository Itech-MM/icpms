package org.flexitech.projects.icpms.api.controllers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.flexitech.projects.icpms.api.security.JwtService;
import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.api.security.OperatorUserDetailsService;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.api.request.auth.LoginRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.auth.LogoutRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.auth.RefreshTokenRequestDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.auth.AuthResponseDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthApiController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final OperatorUserDetailsService operatorUserDetailsService;
	private final OperatorShiftService operatorShiftService;

	@Value("${app.jwt.expiration-ms}")
	private long jwtExpirationMs;

	public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService,
			OperatorUserDetailsService operatorUserDetailsService, OperatorShiftService operatorShiftService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.operatorUserDetailsService = operatorUserDetailsService;
		this.operatorShiftService = operatorShiftService;
	}

	@PostMapping("/login")
	public ApiResponse<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
			HttpServletRequest httpRequest) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		} catch (BadCredentialsException e) {
			return ApiResponse.error("Invalid username or password.");
		}

		OperatorPrincipal principal = (OperatorPrincipal) authentication.getPrincipal();

		List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		String sessionToken = UUID.randomUUID().toString();
		String accessToken = jwtService.generateToken(principal.getUsername(), principal.getAuthorities(), Set.of(),
				sessionToken);
		String refreshToken = jwtService.generateRefreshToken(principal.getUsername());

		boolean startShift = needsShiftStart(principal, httpRequest);

		AuthResponseDTO response = new AuthResponseDTO(accessToken, refreshToken, principal.getUsername(), roles,
				jwtExpirationMs, startShift);

		return ApiResponse.ok(response, "Login successful.");
	}

	@PostMapping("/refresh")
	public ApiResponse<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request,
			HttpServletRequest httpRequest) {
		String token = request.getRefreshToken();

		if (!jwtService.validateToken(token) || !jwtService.isRefreshToken(token)) {
			return ApiResponse.error("Invalid or expired refresh token.");
		}

		String username = jwtService.extractUsername(token);
		UserDetails userDetails = operatorUserDetailsService.loadUserByUsername(username);
		OperatorPrincipal principal = (OperatorPrincipal) userDetails;

		List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		String sessionToken = UUID.randomUUID().toString();
		String newAccessToken = jwtService.generateToken(principal.getUsername(), principal.getAuthorities(), Set.of(),
				sessionToken);
		String newRefreshToken = jwtService.generateRefreshToken(principal.getUsername());

		boolean startShift = needsShiftStart(principal, httpRequest);

		AuthResponseDTO response = new AuthResponseDTO(newAccessToken, newRefreshToken, principal.getUsername(), roles,
				jwtExpirationMs, startShift);

		return ApiResponse.ok(response, "Token refreshed.");
	}

	@PostMapping("/logout")
	public ApiResponse<Void> logout(@RequestBody(required = false) LogoutRequestDTO request,
			HttpServletRequest httpRequest) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof OperatorPrincipal principal) {
			endActiveShiftIfPresent(principal, request, httpRequest);
		}

		SecurityContextHolder.clearContext();
		return ApiResponse.ok(null, "Logged out.");
	}

	private void endActiveShiftIfPresent(OperatorPrincipal principal, LogoutRequestDTO request,
			HttpServletRequest httpRequest) {

		if (request == null || request.getEndShift() == null || !request.getEndShift())
			return;

		String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
		Long operatorId = principal.getOperator().getId();

		if (!CommonValidators.validString(gateIpAddress)) {
			log.warn("Logout for operator {} had no gate header - skipping shift close.", operatorId);
			return;
		}

		OperatorShiftDTO activeShift;
		try {
			activeShift = operatorShiftService.getActiveShiftByOperator(operatorId, gateIpAddress);
		} catch (IllegalStateException e) {
			log.warn("Could not resolve gate for operator {} on logout: {}", operatorId, e.getMessage());
			return;
		} catch (Exception e) {
			log.error("Unexpected error checking active shift for operator {} on logout: {}", operatorId,
					e.getMessage(), e);
			return;
		}

		if (activeShift == null) {
			return;
		}

		OperatorShiftDTO toEnd = new OperatorShiftDTO();
		toEnd.setId(activeShift.getId());
		toEnd.setGateIpAddress(gateIpAddress);
		/* toEnd.setClosingCash(request != null ? request.getClosingCash() : null); */
		toEnd.setRemark(request != null ? request.getRemark() : null);

		try {
			OperatorShiftDTO ended = operatorShiftService.endShift(toEnd);
			log.info("Ended shift {} for operator {} on logout - diff: {}", activeShift.getId(), operatorId,
					ended.getDiff());
		} catch (IllegalStateException e) {
			log.warn("Failed to end shift {} for operator {} on logout: {}", activeShift.getId(), operatorId,
					e.getMessage());
		}
	}

	private boolean needsShiftStart(OperatorPrincipal principal, HttpServletRequest httpRequest) {
		String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);

		try {
			OperatorShiftDTO activeShift = operatorShiftService
					.getActiveShiftByOperator(principal.getOperator().getId(), gateIpAddress);
			return activeShift == null;
		} catch (Exception e) {
			log.error("Shift lookup failed for operator {} during login/refresh: {}", principal.getOperator().getId(),
					e.getMessage(), e);
			return true;
		}
	}
}