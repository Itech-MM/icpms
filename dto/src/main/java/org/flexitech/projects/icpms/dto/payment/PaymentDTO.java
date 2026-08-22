package org.flexitech.projects.icpms.dto.payment;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.PaymentMethod;
import org.flexitech.projects.icpms.common.enums.PaymentStatus;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.payment.Payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO extends CommonDTO {

	@NotNull
	private Long sessionId;
	private String plateNumber;
	private String siteName;
	@NotNull
	private BigDecimal amount;
	@NotNull
	private Integer method;
	private String methodDesc;
	private String paymentTime;
	private String referenceNo;
	private Integer status = 2;
	private String statusDesc;

	public PaymentDTO(Payment payment) {
		super(payment);
		if (CommonValidators.isValidObject(payment.getSession())) {
			this.sessionId = payment.getSession().getId();
			if (CommonValidators.isValidObject(payment.getSession().getVehicle())) {
				this.plateNumber = payment.getSession().getVehicle().getPlateNumber();
			}
			if (CommonValidators.isValidObject(payment.getSession().getEntryGate())
					&& CommonValidators.isValidObject(payment.getSession().getEntryGate().getSite())) {
				this.siteName = payment.getSession().getEntryGate().getSite().getName();
			}
		}
		this.amount = payment.getAmount();
		this.method = payment.getMethod();
		this.methodDesc = PaymentMethod.getDescByCode(method);
		if (CommonValidators.isValidObject(payment.getPaymentTime())) {
			this.paymentTime = DateUtils.dateToString(payment.getPaymentTime(), CommonConstants.STANDARD_24_HOUR_DATE_FORMAT2);
		}
		this.referenceNo = payment.getReferenceNo();
		this.status = payment.getStatus();
		this.statusDesc = PaymentStatus.getDescByCode(status);
	}
}
