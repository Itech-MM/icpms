package org.flexitech.projects.icpms.service.menu;

import java.util.List;

import org.flexitech.projects.icpms.dto.menu.MenuDTO;
import org.flexitech.projects.icpms.dto.menu.MenuRoleAccessDTO;
import org.flexitech.projects.icpms.dto.menu.MenuRoleAccessRequest;
import org.flexitech.projects.icpms.dto.menu.MenuRoleAccessTreeNode;

public interface MenuRoleAccessService {
    List<MenuRoleAccessTreeNode> getMenuTreeForRole(Long roleId);
    void saveOrUpdatePermission(MenuRoleAccessRequest request);
    void saveBulkPermissions(List<MenuRoleAccessRequest> requests);
    void copyPermissions(Long sourceRoleId, Long targetRoleId);
    void deletePermission(Long id);
    
    List<MenuRoleAccessDTO> getMenuAccessByRoleId(Long roleId);
    MenuRoleAccessDTO getMenuAccessById(Long id);
    MenuRoleAccessDTO saveMenuAccess(MenuRoleAccessDTO dto);
    void saveBulkMenuAccess(Long roleId, List<Long> menuIds,
                          List<Boolean> canView, List<Boolean> canAccess,
                          List<Boolean> canEdit, List<Boolean> canDelete);
    void deleteMenuAccess(Long id);
    List<MenuDTO> getMenusWithoutAccessForRole(Long roleId);
}