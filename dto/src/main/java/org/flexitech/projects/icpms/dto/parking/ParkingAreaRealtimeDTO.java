package org.flexitech.projects.icpms.dto.parking;

import java.util.List;

import org.flexitech.projects.icpms.dto.slot.ParkingSlotDTO;
import org.flexitech.projects.icpms.persistence.entities.parking.ParkingArea;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingAreaRealtimeDTO extends ParkingAreaDTO {

	private List<ParkingSlotDTO> slots;

	public ParkingAreaRealtimeDTO(ParkingArea p) {
		super(p);
	}
}