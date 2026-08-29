package org.flexitech.projects.icpms.dto.operator;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OperatorShiftSummaryDTO {
	private String code;
	private Integer totalTransactions;
	private BigDecimal totalAmount;
	private String totalAmountDesc;
	private Integer totalIncompleteTransactions;
	private Integer totalCompletedTransactions;
}
