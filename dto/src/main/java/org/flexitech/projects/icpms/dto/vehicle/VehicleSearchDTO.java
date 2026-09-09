package org.flexitech.projects.icpms.dto.vehicle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehicleSearchDTO {
	private String plateNumber;
	private Integer status;
	private Integer fromSession; // 1=yes,2=no
	private Long parkingAreaId;
}
