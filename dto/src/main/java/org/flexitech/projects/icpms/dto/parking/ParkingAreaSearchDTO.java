package org.flexitech.projects.icpms.dto.parking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingAreaSearchDTO {

	private String name;
	private Long gateId;
	private Integer status;

}