package org.flexitech.projects.icpms.dto.api.request.member;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChargeResultDTO {

	private boolean success;
	private BigDecimal chargedAmount;
	private BigDecimal remainingBalance;
	private Long paymentId;
	private Long subscriptionId;
	private String failureReason;

	public static ChargeResultDTO success(BigDecimal chargedAmount, BigDecimal remainingBalance, Long paymentId, Long subscriptionId) {
		ChargeResultDTO dto = new ChargeResultDTO();
		dto.setSuccess(true);
		dto.setChargedAmount(chargedAmount);
		dto.setRemainingBalance(remainingBalance);
		dto.setPaymentId(paymentId);
		dto.setSubscriptionId(subscriptionId);
		return dto;
	}

	public static ChargeResultDTO failure(String reason) {
		ChargeResultDTO dto = new ChargeResultDTO();
		dto.setSuccess(false);
		dto.setFailureReason(reason);
		return dto;
	}
}