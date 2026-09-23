package org.flexitech.projects.icpms.service.menu;

import java.util.List;

import org.flexitech.projects.icpms.dto.menu.MenuDTO;

public interface MenuService {
	List<MenuDTO> getAllMenus(Integer status);
	
	List<MenuDTO> getMenuTreeForUser(Long userId);
}
