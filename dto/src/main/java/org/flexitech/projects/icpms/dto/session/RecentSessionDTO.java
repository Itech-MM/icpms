package org.flexitech.projects.icpms.dto.session;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.PlateNumberValidator;
import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecentSessionDTO {
	private Long id;
	private String plateNumber;
	private Boolean isUnknownPlate;
	private String gateName;
	private String time;
	private Integer status;
	private String statusDesc;
	private Boolean isMember;
	private Boolean isVip;

	public RecentSessionDTO(ParkingSession session) {
		if (session == null) return;

		this.id = session.getId();

		Vehicle vehicle = session.getVehicle();
		this.plateNumber = vehicle != null ? vehicle.getPlateNumber() : null;
		this.isUnknownPlate = PlateNumberValidator.isUnknownOrInvalid(this.plateNumber);

		boolean isCompleted = ParkingSessionStatus.COMPLETED.getCode().equals(session.getStatus());

		if (isCompleted && session.getExitGate() != null) {
			this.gateName = session.getExitGate().getName();
			this.time = DateUtils.dateToString(session.getExitTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		} else if (session.getEntryGate() != null) {
			this.gateName = session.getEntryGate().getName();
			this.time = DateUtils.dateToString(session.getEntryTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}

		this.status = session.getStatus();
		this.statusDesc = ParkingSessionStatus.getDescByCode(this.status);

		this.isMember = vehicle != null && vehicle.getMember() != null;
		this.isVip = this.isMember && Boolean.TRUE.equals(vehicle.getMember().getIsVip());
	}
}