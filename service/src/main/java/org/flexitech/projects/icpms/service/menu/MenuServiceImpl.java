package org.flexitech.projects.icpms.service.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.menu.MenuDTO;
import org.flexitech.projects.icpms.persistence.entities.menu.Menu;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.menu.MenuRepository;
import org.flexitech.projects.icpms.persistence.repositories.menu.MenuRoleAccessRepository;
import org.flexitech.projects.icpms.persistence.repositories.user.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MenuServiceImpl implements MenuService {

	private final MenuRepository menuRepository;
	private final UserRepository userRepository;
	private final MenuRoleAccessRepository menuRoleAccessRepository;

	public MenuServiceImpl(MenuRepository menuRepository, UserRepository userRepository, MenuRoleAccessRepository menuRoleAccessRepository) {
		this.menuRepository = menuRepository;
		this.userRepository = userRepository;
		this.menuRoleAccessRepository = menuRoleAccessRepository;
	}

	@Override
	public List<MenuDTO> getAllMenus(Integer status) {
		return menuRepository.findByStatusOrderBySequenceAsc(status).stream().map(MenuDTO::new).toList();
	}
	
	@Override
	public List<MenuDTO> getMenuTreeForUser(Long userId) {
		
        List<MenuDTO> menus = getMenusForUser(userId);
        
        Map<Long, MenuDTO> menuMap = new HashMap<>();
        List<MenuDTO> rootMenus = new ArrayList<>();

        for (MenuDTO menu : menus) {
            menuMap.put(menu.getId(), menu);
            if (menu.getParentMenuId() == null) {
                rootMenus.add(menu);
            }
        }

        for (MenuDTO menu : menus) {
            if (menu.getParentMenuId() != null) {
                MenuDTO parent = menuMap.get(menu.getParentMenuId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        rootMenus.sort(Comparator.comparing(MenuDTO::getSequence));
        rootMenus.forEach(menu -> 
            menu.getChildren().sort(Comparator.comparing(MenuDTO::getSequence))
        );

        return rootMenus;
    }

    public boolean hasAccessToMenu(User user, String menuUrl) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        List<Long> roleIds = Arrays.asList(user.getRole().getId());

        return menuRoleAccessRepository.existsByMenuUrlAndRoleIds(menuUrl, roleIds);
    }

    
    
    public List<MenuDTO> getMenusForUser(Long userId) {
    	User user = this.userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
		
        if (user == null || user.getRole() == null) {
            return Collections.emptyList();
        }

        List<Long> accessibleMenuIds = menuRoleAccessRepository
                .findAccessibleMenuIdsByRoleIds(Arrays.asList(user.getRole().getId()));

        if (accessibleMenuIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Menu> accessibleMenus = menuRepository
                .findByIdInAndStatusAndDisplayStatusOrderBySequenceAsc(accessibleMenuIds, ActiveStatus.ACTIVE.getCode(), ActiveStatus.ACTIVE.getCode());

        return buildMenuTree(accessibleMenus);
    }
    

    
    private List<MenuDTO> buildMenuTree(List<Menu> menus) {
        return menus.stream()
                .map(MenuDTO::new)
                .collect(Collectors.toList());
    }

}
