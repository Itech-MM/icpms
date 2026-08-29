package org.flexitech.projects.icpms.dto.parking;

import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.parking.ParkingArea;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingAreaDTO extends CommonDTO {

	private String name;

	private String remark;

	private Integer totalSlot;

	private Integer vipSlot;

	private Integer normalSlot;

	private Integer availableTotalSlot;

	private Integer availableTotalVipSlot;

	private Integer availableTotalNormalSlot;

	private Boolean slotTrackingEnabled;

	private Integer status;

	private String statusDesc;
	

	private Long tariffId;
	private String tariffName;


	public ParkingAreaDTO(ParkingArea p) {
		super(p);

		this.name = p.getName();
		this.remark = p.getRemark();

		this.totalSlot = p.getTotalSlot();
		this.vipSlot = p.getVipSlot();
		this.normalSlot = p.getNormalSlot();

		this.availableTotalSlot = p.getAvailableTotalSlot();
		this.availableTotalVipSlot = p.getAvailableTotalVipSlot();
		this.availableTotalNormalSlot = p.getAvailableTotalNormalSlot();

		this.slotTrackingEnabled = p.getSlotTrackingEnabled();
		this.status = p.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
		
		if(p.getTariff() != null) {
			this.tariffId = p.getTariff().getId();
			this.tariffName = p.getTariff().getName();
		}
		
	}
}