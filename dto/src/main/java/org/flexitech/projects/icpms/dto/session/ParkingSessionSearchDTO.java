package org.flexitech.projects.icpms.dto.session;

import org.flexitech.projects.icpms.dto.CommonSearchDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingSessionSearchDTO extends CommonSearchDTO{
	private String plateNumber;
	private Long siteId;
	private Integer status;
	private String fromDate;
	private String toDate;
	private Long gateId;
	private Long activeShiftId;
}
