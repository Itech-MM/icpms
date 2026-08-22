package org.flexitech.projects.icpms.dto.slot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingSlotSearchDTO {
	private String slotNumber;
	private Long siteId;
	private Boolean isVip;
	private Integer status;
}
