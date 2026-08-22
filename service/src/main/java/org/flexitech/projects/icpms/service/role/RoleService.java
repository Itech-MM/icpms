package org.flexitech.projects.icpms.service.role;

import java.util.List;

import org.flexitech.projects.icpms.dto.role.RoleDTO;

public interface RoleService {
	RoleDTO manageRole(RoleDTO roleDTO) throws Exception;
	RoleDTO getRoleById(Long id) throws Exception;
	List<RoleDTO> getAllRoles() throws Exception;
	boolean deleteRole(Long id) throws Exception;
	List<RoleDTO> findAllActiveRoles();
}
