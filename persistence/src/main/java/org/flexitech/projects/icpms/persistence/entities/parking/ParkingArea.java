package org.flexitech.projects.icpms.persistence.entities.parking;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.tariff.Tariff;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = TableNames.PARKING_AREA_TBL)
@Getter
@Setter
@NoArgsConstructor
public class ParkingArea extends BasedEntity {

	private String name;

	private String remark;

	@Column(name = "total_slot")
	private Integer totalSlot;

	@Column(name = "vip_slot")
	private Integer vipSlot;

	@Column(name = "available_total_slot")
	private Integer availableTotalSlot;

	@Column(name = "available_total_vip_slot")
	private Integer availableTotalVipSlot;

	@Column(name = "slot_tracking_enabled")
	private Boolean slotTrackingEnabled = false;

	private Integer status;
	

	@ManyToOne
	@JoinColumn(name = "tariff_id")
	private Tariff tariff;

	@Transient
	public Integer getNormalSlot() {
		if (totalSlot == null || vipSlot == null) {
			return null;
		}
		return totalSlot - vipSlot;
	}

	@Transient
	public Integer getAvailableTotalNormalSlot() {
		if (availableTotalSlot == null || availableTotalVipSlot == null) {
			return null;
		}
		return availableTotalSlot - availableTotalVipSlot;
	}

}