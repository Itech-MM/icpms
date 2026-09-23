package org.flexitech.projects.icpms.admin.controller.advice;

import java.util.Collections;
import java.util.List;

import org.flexitech.projects.icpms.dto.menu.MenuDTO;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.menu.MenuService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MenuService menuService;
    private final AuthenticationService authenticationService;

    /**
     * Add menus to model for all authenticated requests
     */
    @ModelAttribute("menus")
    public List<MenuDTO> addMenusToModel() {
        User user = authenticationService.getLoggedInUser();
        if (user != null) {
            return menuService.getMenuTreeForUser(user.getId());
        }
        return Collections.emptyList();
    }

    /**
     * Add current user info to model
     */
    @ModelAttribute("currentUser")
    public User addCurrentUserToModel() {
        return authenticationService.getLoggedInUser();
    }
}