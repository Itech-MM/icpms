package org.flexitech.projects.icpms.dto.api.response.visitor;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitorExitPreviewResponseDTO {

	private Long sessionId;
	private String plateNumber;
	private String entryTime;
	private long durationMinutes;
	private String tariffName;
	private BigDecimal amountDue;
	private String amountDueDesc;
}