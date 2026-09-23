package org.flexitech.projects.icpms.service.menu;

public interface MenuSecurityService {
	boolean hasMenuAccess(String menuCode);
	boolean hasMenuView(String menuCode);
	boolean hasMenuEdit(String menuCode);
	boolean hasMenuDelete(String menuCode);
	boolean hasAnyMenuPermission(String menuCode);
	boolean hasFullMenuAccess(String menuCode);
}
