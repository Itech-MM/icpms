package org.flexitech.projects.icpms.dto.api.request.visitor;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorEntryRequestDTO {

	@NotBlank(message = "Plate number is required")
	private String plateNumber;

	private String vehicleType;

	private Long parkingSlotId;	
}