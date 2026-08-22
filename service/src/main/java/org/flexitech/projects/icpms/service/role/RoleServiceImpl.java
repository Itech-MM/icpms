package org.flexitech.projects.icpms.service.role;

import java.util.Date;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.role.RoleDTO;
import org.flexitech.projects.icpms.persistence.entities.role.Role;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.role.RoleRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RoleServiceImpl implements RoleService {
	
	private final RoleRepository roleRepository;
	private final AuthenticationService authenticationService;

	public RoleServiceImpl(RoleRepository roleRepository, AuthenticationService authenticationService) {
		this.roleRepository = roleRepository;
		this.authenticationService = authenticationService;
	}
	
	@Override
	public RoleDTO manageRole(RoleDTO roleDTO) throws Exception {
		Role role = null;
		User user = this.authenticationService.getLoggedInUser();
		if(CommonValidators.validLong(roleDTO.getId())) {
			role = this.roleRepository.findById(roleDTO.getId())
					.orElseThrow(()-> new EntityNotFoundException("Role doesn't exist!"));
			role.setUpdatedTime(new Date());
			role.setCreatedBy(user);
		}else {
			role = new Role();
			role.setCreatedTime(new Date());
			role.setUpdatedBy(user);
		}
		role.setName(roleDTO.getName());
		role.setCode(roleDTO.getCode());
		
		Role saved = this.roleRepository.save(role);
		return new RoleDTO(saved);
	}

	@Override
	public List<RoleDTO> getAllRoles() throws Exception {
		return this.roleRepository.findAll().stream().map(RoleDTO::new).toList();
	}

	@Override
	public boolean deleteRole(Long id) throws Exception {
		
		Role role = this.roleRepository.findById(id)
				.orElseThrow(()-> new EntityNotFoundException("Role doesn't exist!"));
		
		this.roleRepository.delete(role);
		
		return true;
	}

	@Override
	public RoleDTO getRoleById(Long id) throws Exception {
		Role role = this.roleRepository.findById(id)
				.orElseThrow(()-> new EntityNotFoundException("Role doesn't exist!"));
		return new RoleDTO(role);
	}

	@Override
	public List<RoleDTO> findAllActiveRoles() {
		return this.roleRepository.findByStatus(ActiveStatus.ACTIVE.getCode()).stream().map(RoleDTO::new).toList();
	}

}
