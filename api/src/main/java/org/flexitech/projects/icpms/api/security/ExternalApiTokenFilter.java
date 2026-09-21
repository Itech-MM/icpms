package org.flexitech.projects.icpms.api.security;

import java.io.IOException;

import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.service.external_client.ExternalApiTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ExternalApiTokenFilter extends OncePerRequestFilter {

	public static final String EXTERNAL_TOKEN_HEADER = "X-External-Token";

	private final ExternalApiTokenService externalApiTokenService;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = request.getHeader(EXTERNAL_TOKEN_HEADER);

		if (!externalApiTokenService.isValidToken(token)) {
			log.warn("Rejected external-api request from {} to {} - invalid or missing token.",
			        request.getRemoteAddr(), request.getRequestURI());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(objectMapper
					.writeValueAsString(new ApiResponse<Void>(false, "Unauthorized - invalid external API token.", null)));
			return;
		}

		filterChain.doFilter(request, response);
	}
}