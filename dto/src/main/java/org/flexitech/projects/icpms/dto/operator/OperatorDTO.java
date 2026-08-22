package org.flexitech.projects.icpms.dto.operator;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.OperatorRole;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;

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
public class OperatorDTO extends CommonDTO {

	@NotBlank
	private String name;
	@NotBlank
	private String username;
	private String password;
	private String phoneNumber;
	private Long siteId;
	private String siteName;
	@NotNull
	private Integer role;
	private String roleDesc;
	private Integer status = 1;
	private String statusDesc;

	public OperatorDTO(Operator operator) {
		super(operator);
		this.name = operator.getName();
		this.username = operator.getUsername();
		this.phoneNumber = operator.getPhoneNumber();
		if (CommonValidators.isValidObject(operator.getSite())) {
			this.siteId = operator.getSite().getId();
			this.siteName = operator.getSite().getName();
		}
		this.role = operator.getRole();
		this.roleDesc = OperatorRole.getDescByCode(role);
		this.status = operator.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
	}
}
