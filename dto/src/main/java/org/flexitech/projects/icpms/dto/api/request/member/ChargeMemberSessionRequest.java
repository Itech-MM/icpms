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
public class ChargeMemberSessionRequest {

	@NotNull
	private Long memberId;
	@NotNull
	private Long sessionId;
	@NotNull
	private BigDecimal amount;
}