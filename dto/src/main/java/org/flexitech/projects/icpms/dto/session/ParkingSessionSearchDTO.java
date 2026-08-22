package org.flexitech.projects.icpms.dto.session;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingSessionSearchDTO {
	private String plateNumber;
	private Long siteId;
	private Integer status;
	private String fromDate;
	private String toDate;
}
