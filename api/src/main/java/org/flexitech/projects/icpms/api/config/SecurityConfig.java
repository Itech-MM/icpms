package org.flexitech.projects.icpms.api.config;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.api.security.ExternalApiTokenFilter;
import org.flexitech.projects.icpms.api.security.JwtAuthenticationFilter;
import org.flexitech.projects.icpms.api.security.JwtService;
import org.flexitech.projects.icpms.api.security.OperatorUserDetailsService;
import org.flexitech.projects.icpms.common.ApiErrorCode;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.service.external_client.ExternalApiTokenService;
import org.flexitech.projects.icpms.service.operator.OperatorShiftService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final OperatorUserDetailsService operatorUserDetailsService;
	private final ObjectMapper objectMapper;
	private final PasswordEncoder passwordEncoder;

	public SecurityConfig(OperatorUserDetailsService operatorUserDetailsService,
			ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
		this.operatorUserDetailsService = operatorUserDetailsService;
		this.objectMapper = objectMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
			OperatorUserDetailsService operatorUserDetailsService,
			OperatorShiftService operatorShiftService,
			ObjectMapper objectMapper) {
		return new JwtAuthenticationFilter(jwtService, operatorUserDetailsService, operatorShiftService, objectMapper);
	}

	@Bean
	public ExternalApiTokenFilter externalApiTokenFilter(ExternalApiTokenService externalApiTokenService,
			ObjectMapper objectMapper) {
		return new ExternalApiTokenFilter(externalApiTokenService, objectMapper);
	}

	@Bean
	public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
			JwtAuthenticationFilter filter) {
		FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean<ExternalApiTokenFilter> externalApiTokenFilterRegistration(
			ExternalApiTokenFilter filter) {
		FilterRegistrationBean<ExternalApiTokenFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@SuppressWarnings("deprecation")
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(operatorUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain externalApiFilterChain(HttpSecurity http,
			ExternalApiTokenFilter externalApiTokenFilter) throws Exception {
		http.securityMatcher("/external-api/**")
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.addFilterBefore(externalApiTokenFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						auth -> auth
								.requestMatchers("/api/auth/login", "/api/auth/validate", "/api/auth/refresh", "/api/auth/methods", "/actuator/health",
										"/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
										"/ws/**")
								.permitAll().anyRequest().authenticated())
				.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) -> {
					log.error("Unauthorized error:: {}", ExceptionUtils.getStackTrace(authException));
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					response.getWriter().write(objectMapper
							.writeValueAsString(new ApiResponse<Void>(false, "Unauthorized - please log in again.", ApiErrorCode.UNAUTHORIZED, null)));
				}))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}