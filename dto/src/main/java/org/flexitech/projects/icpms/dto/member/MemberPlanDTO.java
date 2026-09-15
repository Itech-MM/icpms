package org.flexitech.projects.icpms.dto.member;

import java.math.BigDecimal;
import java.util.Map;

import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.member.MemberPlan;

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
public class MemberPlanDTO extends CommonDTO {

	@NotBlank
	private String code;
	@NotBlank
	private String name;
	private String description;
	@NotNull
	private BigDecimal price;
	@NotNull
	private Integer durationDays;
	private BigDecimal grantedBalance;
	private BigDecimal discountPercent;
	private Integer freeMinutes;
	private Integer maxVehicles;
	private Boolean isActive = true;
	private Integer status = 1;
	private String statusDesc;
	private Map<String, Object> extraFeatures;

	public MemberPlanDTO(MemberPlan plan) {
		super(plan);
		this.code = plan.getCode();
		this.name = plan.getName();
		this.description = plan.getDescription();
		this.price = plan.getPrice();
		this.durationDays = plan.getDurationDays();
		this.grantedBalance = plan.getGrantedBalance();
		this.discountPercent = plan.getDiscountPercent();
		this.freeMinutes = plan.getFreeMinutes();
		this.maxVehicles = plan.getMaxVehicles();
		this.isActive = plan.getIsActive();
		this.status = plan.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
		this.extraFeatures = plan.getExtraFeatures();
	}
}