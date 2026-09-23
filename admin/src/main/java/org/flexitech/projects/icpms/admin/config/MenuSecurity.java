package org.flexitech.projects.icpms.admin.config;

import org.flexitech.projects.icpms.common.exceptions.MenuAccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("menuSecurity")
@Slf4j
public class MenuSecurity {
	
    public boolean hasMenuAccess(String menuCode) throws MenuAccessDeniedException {
        return checkMenuPermission(menuCode, "ACCESS", true);
    }

    public boolean hasMenuView(String menuCode)  throws MenuAccessDeniedException {
        return checkMenuPermission(menuCode, "VIEW", true);
    }
    
    public boolean hasMenuEdit(String menuCode) throws MenuAccessDeniedException {
        return checkMenuPermission(menuCode, "EDIT", true);
    }

    public boolean hasMenuDelete(String menuCode)  throws MenuAccessDeniedException {
        return checkMenuPermission(menuCode, "DELETE", true);
    }
    
    public boolean checkMenuAccess(String menuCode){
        try {
			return checkMenuPermission(menuCode, "ACCESS", false);
		} catch (MenuAccessDeniedException e) {
			return false;
		}
    }

    public boolean checkMenuView(String menuCode) {
        try {
			return checkMenuPermission(menuCode, "VIEW", false);
		} catch (MenuAccessDeniedException e) {
			return false;
		}
    }
    
    public boolean checkMenuEdit(String menuCode){
        try {
			return checkMenuPermission(menuCode, "EDIT", false);
		} catch (MenuAccessDeniedException e) {
			return false;
		}
    }

    public boolean checkMenuDelete(String menuCode){
        try {
			return checkMenuPermission(menuCode, "DELETE", false);
		} catch (MenuAccessDeniedException e) {
			return false;
		}
    }
    
    public void checkMenuAccessAndView(String menuCode) throws MenuAccessDeniedException {
        if (!hasMenuAccess(menuCode) || !hasMenuView(menuCode)) {
            throw new MenuAccessDeniedException(menuCode, "ACCESS and VIEW");
        }
    }
    
    private boolean checkMenuPermission(String menuCode, String permission, boolean throwException) throws MenuAccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || 
            !authentication.isAuthenticated() || 
            authentication.getPrincipal() instanceof String || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            if (throwException) {
                throw new MenuAccessDeniedException(menuCode, permission);
            }
            return false;
        }

        String requiredAuthority = "MENU_" + permission + "_" + menuCode;
        boolean hasPermission = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals(requiredAuthority));
        
        if (!hasPermission && throwException) {
            throw new MenuAccessDeniedException(menuCode, permission);
        }
        
        return hasPermission;
    }
}
