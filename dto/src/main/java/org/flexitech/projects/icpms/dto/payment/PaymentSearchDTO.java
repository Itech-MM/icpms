package org.flexitech.projects.icpms.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentSearchDTO {
	private String plateNumber;
	private Integer method;
	private Integer status;
	private String fromDate;
	private String toDate;
}
