package org.flexitech.projects.icpms.api.security;

import java.io.IOException;
import java.util.Set;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Set<String> SHIFT_CHECK_EXEMPT_PATHS = Set.of(
			"/api/shifts/start"
	);

	private final JwtService jwtService;
	private final OperatorUserDetailsService operatorUserDetailsService;
	private final OperatorShiftService operatorShiftService;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		
		log.debug("auth:: {}", authHeader);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String token = authHeader.substring(7);
		final String username;

		try {
			if (!jwtService.isAccessToken(token)) {
				filterChain.doFilter(request, response);
				return;
			}
			username = jwtService.extractUsername(token);
		} catch (Exception e) {
			log.debug("JWT parsing failed: {}", e.getMessage());
			filterChain.doFilter(request, response);
			return;
		}

		if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}

		final UserDetails userDetails;
		try {
			userDetails = operatorUserDetailsService.loadUserByUsername(username);
		} catch (Exception e) {
			log.warn("Failed to load user '{}': {}", username, e.getMessage());
			filterChain.doFilter(request, response);
			return;
		}

		if (!jwtService.isTokenValid(token, userDetails.getUsername())) {
			filterChain.doFilter(request, response);
			return;
		}

		boolean shiftCheckExempt = SHIFT_CHECK_EXEMPT_PATHS.contains(request.getServletPath());

		if (userDetails instanceof OperatorPrincipal op && !shiftCheckExempt) {
			String gateIpAddress = request.getHeader(CommonConstants.GATE_IP_HEADER);

			OperatorShiftDTO activeShift;
			try {
				activeShift = operatorShiftService.getActiveShiftByOperator(op.getOperator().getId(), gateIpAddress);
			} catch (Exception e) {
				log.error("Shift lookup failed for operator {} at gate {}: {}", op.getOperator().getId(), gateIpAddress,
						e.getMessage(), e);
				writeError(response, HttpStatus.INTERNAL_SERVER_ERROR,
						"Unable to verify shift status. Please try again.");
				return;
			}

			if (activeShift == null) {
				log.warn("No active shift for operator {} at gate {}", op.getOperator().getId(), gateIpAddress);
				writeError(response, HttpStatus.UNAUTHORIZED, "Unauthorized - Please open shift.");
				return;
			}
		}

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
	    String path = request.getServletPath();
	    return path.equals("/api/auth/login")
	            || path.equals("/api/auth/refresh")
	            || path.equals("/api/auth/validate")
	            || path.equals("/actuator/health")
	            || path.equals("/swagger-ui.html")
	            || path.startsWith("/swagger-ui/")
	            || path.startsWith("/v3/api-docs")
	            || path.startsWith("/ws");
	}

	private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
		if (response.isCommitted()) {
			log.warn("Response already committed, cannot write error: {}", message);
			return;
		}
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<Void>(false, message, null)));
	}
}