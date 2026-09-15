package org.flexitech.projects.icpms.dto.api.request.member;

import java.math.BigDecimal;
import java.util.Map;

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
public class MemberPlanRequest {

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
	private Boolean isActive;
	private Map<String, Object> extraFeatures;
}
