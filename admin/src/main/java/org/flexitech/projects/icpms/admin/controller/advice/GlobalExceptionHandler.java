package org.flexitech.projects.icpms.admin.controller.advice;

import org.flexitech.projects.icpms.common.exceptions.MenuAccessDeniedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MenuAccessDeniedException.class)
	public String handleMenuAccessDenied(MenuAccessDeniedException ex, HttpServletRequest request, Model model) {
		log.warn("Menu access denied: {} - {}", ex.getMenuCode(), ex.getRequiredPermission());

		model.addAttribute("menuCode", ex.getMenuCode());
		model.addAttribute("requiredPermission", ex.getRequiredPermission());
		model.addAttribute("errorMessage", "You don't have permission to access this menu");
		model.addAttribute("requestedUrl", request.getRequestURI());

		return "errors/no-access";
	}

	@ExceptionHandler(AccessDeniedException.class)
	public String handleAccessDenied(AccessDeniedException ex, HttpServletRequest request, Model model) {
		log.warn("Access denied for URL: {}", request.getRequestURI());

		model.addAttribute("errorMessage", "Access Denied");
		model.addAttribute("requestedUrl", request.getRequestURI());

		return "errors/no-access";
	}
}