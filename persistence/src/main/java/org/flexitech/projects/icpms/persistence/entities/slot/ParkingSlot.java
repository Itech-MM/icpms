package org.flexitech.projects.icpms.persistence.entities.slot;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.site.Site;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.PARKING_SLOT_TBL)
@Getter
@Setter
public class ParkingSlot extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	@Column(name = "slot_number")
	private String slotNumber;

	@Column(name = "floor_level")
	private String floorLevel;

	@Column(name = "is_vip")
	private Boolean isVip = false;

	/** SlotStatus enum code: 1=Available, 2=Occupied, 3=Reserved, 4=Maintenance */
	private Integer status = 1;
}
