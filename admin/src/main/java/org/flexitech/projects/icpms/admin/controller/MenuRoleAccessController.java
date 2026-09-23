package org.flexitech.projects.icpms.admin.controller;

import java.util.List;

import org.flexitech.projects.icpms.common.MenuCodeConstants;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.menu.CopyPermissionsRequest;
import org.flexitech.projects.icpms.dto.menu.MenuRoleAccessRequest;
import org.flexitech.projects.icpms.dto.menu.MenuRoleAccessTreeNode;
import org.flexitech.projects.icpms.dto.role.RoleDTO;
import org.flexitech.projects.icpms.service.menu.MenuRoleAccessService;
import org.flexitech.projects.icpms.service.role.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/menu-role-access")
@RequiredArgsConstructor
@Slf4j
public class MenuRoleAccessController {

	private final MenuRoleAccessService menuRoleAccessService;
	private final RoleService roleService;

	@GetMapping
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS + "')")
	public String manageAccess(Model model) {
		List<RoleDTO> roles = roleService.findAllActiveRoles();
		model.addAttribute("roles", roles);
		return "menu-role-access/manage";
	}

	@GetMapping("/tree/{roleId}")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS + "')")
	@ResponseBody
	public ResponseEntity<List<MenuRoleAccessTreeNode>> getMenuTreeForRole(@PathVariable Long roleId) {
		try {
			List<MenuRoleAccessTreeNode> tree = menuRoleAccessService.getMenuTreeForRole(roleId);
			return ResponseEntity.ok(tree);
		} catch (Exception e) {
			log.error("Error loading menu tree for role: {}", roleId, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/save-permission")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS + "')")
	@ResponseBody
	public ResponseEntity<ApiResponse<String>> savePermission(@RequestBody MenuRoleAccessRequest request) {
		try {
			menuRoleAccessService.saveOrUpdatePermission(request);
			return ApiResponse.ok(null, "Permission saved successfully");
		} catch (Exception e) {
			log.error("Error saving permission: {}", e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/save-bulk-permissions")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS + "')")
	@ResponseBody
	public ResponseEntity<ApiResponse<String>> saveBulkPermissions(@RequestBody List<MenuRoleAccessRequest> requests) {
		try {
			menuRoleAccessService.saveBulkPermissions(requests);
			return ApiResponse.ok(null, "Permissions saved successfully");
		} catch (Exception e) {
			log.error("Error saving bulk permissions: {}", e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/delete-permission/{id}")
	@PreAuthorize("@menuSecurity.hasMenuDelete('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS + "')")
	@ResponseBody
	public ResponseEntity<ApiResponse<String>> deletePermission(@PathVariable Long id) {
		try {
			menuRoleAccessService.deletePermission(id);
			return ApiResponse.ok(null, "Permission deleted successfully");
		} catch (Exception e) {
			log.error("Error deleting permission: {}", e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@PostMapping("/copy-permissions")
	@PreAuthorize("@menuSecurity.hasMenuEdit('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS + "')")
	@ResponseBody
	public ResponseEntity<ApiResponse<String>> copyPermissions(@RequestBody CopyPermissionsRequest request) {
		try {
			menuRoleAccessService.copyPermissions(request.getSourceRoleId(), request.getTargetRoleId());
			return ApiResponse.ok(null, "Permissions copied successfully");
		} catch (Exception e) {
			log.error("Error copying permissions: {}", e.getMessage());
			return ApiResponse.internalError(e.getMessage());
		}
	}

	@GetMapping("/legacy")
	@PreAuthorize("@menuSecurity.hasMenuAccess('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS
			+ "') or @menuSecurity.hasMenuView('" + MenuCodeConstants.MENU_MENU_ROLE_ACCESS + "')")
	public String legacyManageAccess(Model model, @RequestParam(required = false) Long roleId) {
		List<RoleDTO> roles = roleService.findAllActiveRoles();
		model.addAttribute("roles", roles);

		if (roleId != null) {
			model.addAttribute("selectedRoleId", roleId);
		}

		return "menu-role-access/legacy-manage";
	}
}