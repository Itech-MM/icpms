package org.flexitech.projects.icpms.dto.slot;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.SlotStatus;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSlotDTO extends CommonDTO {

	@NotNull
	private Long siteId;
	private String siteName;
	@NotBlank
	private String slotNumber;
	private String floorLevel;
	private Boolean isVip = false;
	private Integer status = 1;
	private String statusDesc;

	public ParkingSlotDTO(ParkingSlot slot) {
		super(slot);
		if (CommonValidators.isValidObject(slot.getSite())) {
			this.siteId = slot.getSite().getId();
			this.siteName = slot.getSite().getName();
		}
		this.slotNumber = slot.getSlotNumber();
		this.floorLevel = slot.getFloorLevel();
		this.isVip = slot.getIsVip();
		this.status = slot.getStatus();
		this.statusDesc = SlotStatus.getDescByCode(status);
	}
}
