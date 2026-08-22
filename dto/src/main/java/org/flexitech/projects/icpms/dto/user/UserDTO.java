package org.flexitech.projects.icpms.dto.user;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.dto.role.RoleDTO;
import org.flexitech.projects.icpms.persistence.entities.user.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends CommonDTO {

	@NotBlank
	private String name;
	@NotBlank
	private String phoneNumber;
	private String password;
	private RoleDTO role;
	@NotNull
	private Long roleId;
	@NotNull
	private Integer status;
	private String statusDesc;

	public UserDTO(User user) {
		super(user);
		this.name = user.getName();
		this.phoneNumber = user.getPhoneNumber();
		if (CommonValidators.isValidObject(user.getRole())) {
			this.role = new RoleDTO(user.getRole());
			this.roleId = user.getRole().getId();
		}

		this.status = user.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);

	}

}
