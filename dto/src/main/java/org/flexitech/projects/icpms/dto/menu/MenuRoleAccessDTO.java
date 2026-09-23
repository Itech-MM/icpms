package org.flexitech.projects.icpms.dto.menu;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.menu.Menu;
import org.flexitech.projects.icpms.persistence.entities.menu.MenuRoleAccess;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuRoleAccessDTO extends CommonDTO {
	private Long id;
	private Long roleId;
	private String roleName;
	private String roleCode;
	private MenuDTO menu;
	private Long menuId;
	private String menuName;
	private String menuUrl;
	private Boolean canView;
	private Boolean canAccess;
	private Boolean canEdit;
	private Boolean canDelete;
	private Boolean isDefault;
	private Integer permissionPriority;

	private List<MenuRoleAccessDTO> childMenuAccess = new ArrayList<MenuRoleAccessDTO>();

	public MenuRoleAccessDTO(MenuRoleAccess entity) {
		super(entity);
		this.setId(entity.getId());
		this.setRoleId(entity.getRole().getId());
		this.setRoleName(entity.getRole().getName());
		this.setRoleCode(entity.getRole().getCode());
		this.setMenu(new MenuDTO(entity.getMenu()));
		this.setMenuId(entity.getMenu().getId());
		this.setMenuName(entity.getMenu().getName());
		this.setMenuUrl(entity.getMenu().getUrl());
		this.setCanView(entity.getCanView());
		this.setCanAccess(entity.getCanAccess());
		this.setCanEdit(entity.getCanEdit());
		this.setCanDelete(entity.getCanDelete());
		this.setIsDefault(entity.getIsDefault());
		this.setPermissionPriority(entity.getPermissionPriority());
	}

	public static MenuRoleAccessDTO empty(Menu menu) {
		MenuRoleAccessDTO dto = new MenuRoleAccessDTO();

		dto.setId(null);
		dto.setRoleId(null);
		dto.setRoleName(null);
		dto.setRoleCode(null);

		// Set the menu information even when empty
		if (menu != null) {
			dto.setMenu(new MenuDTO(menu));
			dto.setMenuId(menu.getId());
			dto.setMenuName(menu.getName());
			dto.setMenuUrl(menu.getUrl());
		} else {
			dto.setMenu(null);
			dto.setMenuId(null);
			dto.setMenuName(null);
			dto.setMenuUrl(null);
		}

		dto.setCanView(false);
		dto.setCanAccess(false);
		dto.setCanEdit(false);
		dto.setCanDelete(false);

		dto.setIsDefault(false);
		dto.setPermissionPriority(0);

		return dto;
	}

}