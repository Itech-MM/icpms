package org.flexitech.projects.icpms.dto.session;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSessionDTO extends CommonDTO {

	private Long vehicleId;
	private String plateNumber;
	private Long entryGateId;
	private String entryGateName;
	private Long exitGateId;
	private String exitGateName;
	private Long parkingSlotId;
	private String parkingSlotNumber;
	private Long tariffId;
	private String tariffName;
	private Long operatorId;
	private String operatorName;
	private String entryTime;
	private String entryPhotoUrl;
	private String exitTime;
	private String exitPhotoUrl;
	private BigDecimal totalAmount;
	private Integer status = 1;
	private String statusDesc;
	private String siteName;

	public ParkingSessionDTO(ParkingSession session) {
		super(session);
		if (CommonValidators.isValidObject(session.getVehicle())) {
			this.vehicleId = session.getVehicle().getId();
			this.plateNumber = session.getVehicle().getPlateNumber();
		}
		if (CommonValidators.isValidObject(session.getEntryGate())) {
			this.entryGateId = session.getEntryGate().getId();
			this.entryGateName = session.getEntryGate().getName();
			if (CommonValidators.isValidObject(session.getEntryGate().getSite())) {
				this.siteName = session.getEntryGate().getSite().getName();
			}
		}
		if (CommonValidators.isValidObject(session.getExitGate())) {
			this.exitGateId = session.getExitGate().getId();
			this.exitGateName = session.getExitGate().getName();
		}
		if (CommonValidators.isValidObject(session.getParkingSlot())) {
			this.parkingSlotId = session.getParkingSlot().getId();
			this.parkingSlotNumber = session.getParkingSlot().getSlotNumber();
		}
		if (CommonValidators.isValidObject(session.getTariff())) {
			this.tariffId = session.getTariff().getId();
			this.tariffName = session.getTariff().getName();
		}
		if (CommonValidators.isValidObject(session.getOperator())) {
			this.operatorId = session.getOperator().getId();
			this.operatorName = session.getOperator().getName();
		}
		if (CommonValidators.isValidObject(session.getEntryTime())) {
			this.entryTime = DateUtils.dateToString(session.getEntryTime(), CommonConstants.STANDARD_24_HOUR_DATE_FORMAT2);
		}
		if (CommonValidators.isValidObject(session.getExitTime())) {
			this.exitTime = DateUtils.dateToString(session.getExitTime(), CommonConstants.STANDARD_24_HOUR_DATE_FORMAT2);
		}
		this.entryPhotoUrl = session.getEntryPhotoUrl();
		this.exitPhotoUrl = session.getExitPhotoUrl();
		this.totalAmount = session.getTotalAmount();
		this.status = session.getStatus();
		this.statusDesc = ParkingSessionStatus.getDescByCode(status);
	}
}
