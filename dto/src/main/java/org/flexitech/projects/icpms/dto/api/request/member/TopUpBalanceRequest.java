package org.flexitech.projects.icpms.dto.api.request.member;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopUpBalanceRequest {

	@NotNull
	private BigDecimal amount;
	private String remark;
	private Long supervisorId;
}