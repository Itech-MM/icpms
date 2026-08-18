package org.flexitech.projects.icpms.common.exceptions;

import java.nio.file.AccessDeniedException;

public class MenuAccessDeniedException extends AccessDeniedException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7028303505171704704L;
	
	private final String menuCode;
    private final String requiredPermission;
    
    public MenuAccessDeniedException(String menuCode, String requiredPermission) {
        super("Access denied to menu: " + menuCode + " with permission: " + requiredPermission);
        this.menuCode = menuCode;
        this.requiredPermission = requiredPermission;
    }
    
    public String getMenuCode() {
        return menuCode;
    }
    
    public String getRequiredPermission() {
        return requiredPermission;
    }
}