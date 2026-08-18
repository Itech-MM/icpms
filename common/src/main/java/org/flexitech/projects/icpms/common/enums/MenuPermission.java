package org.flexitech.projects.icpms.common.enums;

public enum MenuPermission {
    VIEW("MENU_VIEW"),
    ACCESS("MENU_ACCESS"),
    EDIT("MENU_EDIT"),
    DELETE("MENU_DELETE");
    
    private final String authority;
    
    MenuPermission(String authority) {
        this.authority = authority;
    }
    
    public String getAuthority() {
        return authority;
    }
}