package org.flexitech.projects.icpms.dto.member;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.MembershipType;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.member.Member;

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
public class MemberDTO extends CommonDTO {

	@NotBlank
	private String name;
	@NotBlank
	private String phoneNumber;
	private String email;
	@NotNull
	private Integer membershipType;
	private String membershipTypeDesc;
	private Boolean isVip = false;
	private String validUntil;
	private Long reservedSlotId;
	private String reservedSlotNumber;
	private Integer status = 1;
	private String statusDesc;

	public MemberDTO(Member member) {
		super(member);
		this.name = member.getName();
		this.phoneNumber = member.getPhoneNumber();
		this.email = member.getEmail();
		this.membershipType = member.getMembershipType();
		this.membershipTypeDesc = MembershipType.getDescByCode(membershipType);
		this.isVip = member.getIsVip();
		if (CommonValidators.isValidObject(member.getValidUntil())) {
			this.validUntil = DateUtils.dateToString(member.getValidUntil(), CommonConstants.STANDARD_DB_DATE_FORMAT);
		}
		if (CommonValidators.isValidObject(member.getReservedSlot())) {
			this.reservedSlotId = member.getReservedSlot().getId();
			this.reservedSlotNumber = member.getReservedSlot().getSlotNumber();
		}
		this.status = member.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
	}
}
