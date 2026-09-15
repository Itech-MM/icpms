package org.flexitech.projects.icpms.api.controllers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.api.security.JwtService;
import org.flexitech.projects.icpms.api.security.OperatorLoginAuthenticationService;
import org.flexitech.projects.icpms.api.security.OperatorPrincipal;
import org.flexitech.projects.icpms.api.security.OperatorUserDetailsService;
import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.OperatorAuthMethod;
import org.flexitech.projects.icpms.common.enums.OperatorRole;
import org.flexitech.projects.icpms.dto.api.request.auth.LoginRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.auth.LogoutRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.auth.RefreshTokenRequestDTO;
import org.flexitech.projects.icpms.dto.api.request.auth.SupervisorValidationRequestDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.auth.AuthResponseDTO;
import org.flexitech.projects.icpms.dto.api.response.auth.SupervisorValidationResponseDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingDTO;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.flexitech.projects.icpms.service.setting.SystemSettingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthApiController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final OperatorUserDetailsService operatorUserDetailsService;
	private final OperatorShiftService operatorShiftService;
	private final OperatorLoginAuthenticationService operatorLoginAuthenticationService;
	private final SystemSettingService systemSettingService;

	@Value("${app.jwt.expiration-ms}")
	private long jwtExpirationMs;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request,
			HttpServletRequest httpRequest) {
		OperatorPrincipal principal;

		try {
		    principal = operatorLoginAuthenticationService.authenticate(request);
		} catch (BadCredentialsException e) {
		    return ApiResponse.internalError("Invalid credentials.");
		}
		
		List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

		String sessionToken = UUID.randomUUID().toString();
		String accessToken = jwtService.generateToken(principal.getUsername(), principal.getAuthorities(), Set.of(),
				sessionToken);
		String refreshToken = jwtService.generateRefreshToken(principal.getUsername());

		boolean startShift = needsShiftStart(principal, httpRequest);
		
		OperatorShiftDTO activeShift = null;
		if(!startShift) {
			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			activeShift = operatorShiftService.getActiveShiftByOperator(principal.getOperator().getId(), gateIpAddress);
		}

		AuthResponseDTO response = new AuthResponseDTO(accessToken, refreshToken, principal.getUsername(), 
				principal.getOperator().getName(), principal.getOperator().getId(), roles,
				jwtExpirationMs, startShift, activeShift);

		return ApiResponse.ok(response, "Login successful.");
	}
	
	@GetMapping("/methods")
	public ResponseEntity<ApiResponse<List<SystemSettingDTO>>> getAuthMethods(){
		try {
			
			return ApiResponse.ok(systemSettingService.getSettingByCodeList(List.of(
					OperatorAuthMethod.PASSWORD.getSettingCode(),
					OperatorAuthMethod.RFID.getSettingCode(),
					OperatorAuthMethod.QR_CODE.getSettingCode(),
					OperatorAuthMethod.PIN.getSettingCode(),
					OperatorAuthMethod.MAG_STRIPE.getSettingCode()
					)));
			
		}catch (Exception e) {
			log.error("Error getting auth methods:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/validate-supervisor")
	public ResponseEntity<ApiResponse<SupervisorValidationResponseDTO>> validateSupervisor(
			@Valid @RequestBody SupervisorValidationRequestDTO request, Authentication authentication) {

		if (authentication == null || !(authentication.getPrincipal() instanceof OperatorPrincipal)) {
			return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Operator context is required.");
		}

		Authentication supervisorAuthentication;
		try {
			supervisorAuthentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		} catch (BadCredentialsException e) {
			return ApiResponse.internalError("Invalid supervisor username or password.");
		}

		OperatorPrincipal supervisorPrincipal = (OperatorPrincipal) supervisorAuthentication.getPrincipal();

		if (!OperatorRole.SUPERVISOR.getCode().equals(supervisorPrincipal.getOperator().getRole())) {
			return ApiResponse.error(HttpStatus.FORBIDDEN, "The provided credentials do not belong to a supervisor.");
		}

		SupervisorValidationResponseDTO response = new SupervisorValidationResponseDTO(
				supervisorPrincipal.getOperator().getId(), supervisorPrincipal.getOperator().getName());

		return ApiResponse.ok(response, "Supervisor validated.");
	}
	
	@GetMapping("/validate")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> validate(HttpServletRequest httpRequest, @RequestParam String token) {

	    if (!jwtService.validateToken(token)) {
	        return ApiResponse.badRequest("Token is invalid.");
	    }

	    if (jwtService.isRefreshToken(token)) {
	        return ApiResponse.badRequest("Token is invalid.");
	    }

	    String username = jwtService.extractUsername(token);
	    UserDetails userDetails;
	    try {
	        userDetails = operatorUserDetailsService.loadUserByUsername(username);
	    } catch (Exception e) {
	        return ApiResponse.internalError("Token is invalid.");
	    }

	    OperatorPrincipal principal = (OperatorPrincipal) userDetails;
	    List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

	    boolean startShift = needsShiftStart(principal, httpRequest);

	    OperatorShiftDTO activeShift = null;
	    if (!startShift) {
	        String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
	        activeShift = operatorShiftService.getActiveShiftByOperator(principal.getOperator().getId(), gateIpAddress);
	    }

	    AuthResponseDTO response = new AuthResponseDTO(token, null, principal.getUsername(),principal.getOperator().getName(), principal.getOperator().getId(), roles,
	            jwtExpirationMs, startShift, activeShift);

	    return ApiResponse.ok(response, "Token is valid.");
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthResponseDTO>> refresh(@Valid @RequestBody RefreshTokenRequestDTO request,
			HttpServletRequest httpRequest) {
		String token = request.getRefreshToken();

		if (!jwtService.validateToken(token) || !jwtService.isRefreshToken(token)) {
			return ApiResponse.badRequest("Invalid or expired refresh token.");
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
		OperatorShiftDTO activeShift = null;
		if(!startShift) {
			String gateIpAddress = httpRequest.getHeader(CommonConstants.GATE_IP_HEADER);
			activeShift = operatorShiftService.getActiveShiftByOperator(principal.getOperator().getId(), gateIpAddress);
		}
		AuthResponseDTO response = new AuthResponseDTO(newAccessToken, newRefreshToken, principal.getUsername(),principal.getOperator().getName(), principal.getOperator().getId(), roles,
				jwtExpirationMs, startShift, activeShift);

		return ApiResponse.ok(response, "Token refreshed.");
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) LogoutRequestDTO request,
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