package org.flexitech.projects.icpms.service.menu;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MenuSecurityServiceImpl implements MenuSecurityService{

	@Override
	public boolean hasMenuAccess(String menuCode) {
		return checkMenuPermission(menuCode, "ACCESS");
	}

	@Override
	public boolean hasMenuView(String menuCode) {
		 return checkMenuPermission(menuCode, "VIEW");
	}

	@Override
	public boolean hasMenuEdit(String menuCode) {
		return checkMenuPermission(menuCode, "EDIT");
	}

	@Override
	public boolean hasMenuDelete(String menuCode) {
		return checkMenuPermission(menuCode, "DELETE");
	}

	@Override
	public boolean hasAnyMenuPermission(String menuCode) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().startsWith("MENU_") && 
                              auth.getAuthority().endsWith("_" + menuCode));
	}

	@Override
	public boolean hasFullMenuAccess(String menuCode) {
		return hasMenuView(menuCode) && 
	               hasMenuAccess(menuCode) && 
	               hasMenuEdit(menuCode) && 
	               hasMenuDelete(menuCode);
	}

	private boolean checkMenuPermission(String menuCode, String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        String requiredAuthority = "MENU_" + permission + "_" + menuCode;
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals(requiredAuthority));
    }
}
