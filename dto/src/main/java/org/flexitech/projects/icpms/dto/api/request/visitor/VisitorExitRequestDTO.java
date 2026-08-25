package org.flexitech.projects.icpms.dto.api.request.visitor;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorExitRequestDTO {

	@NotBlank(message = "Plate number is required")
	private String plateNumber;

	private Integer paymentMethod;

	private String referenceNo;
}