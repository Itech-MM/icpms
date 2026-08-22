package org.flexitech.projects.icpms.dto.role;

import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.role.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO extends CommonDTO {

	private String name;
	private String code;

	public RoleDTO(Role r) {
		super(r);
		this.name = r.getName();
		this.code = r.getCode();
	}

}
