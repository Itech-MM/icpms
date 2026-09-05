package org.flexitech.projects.icpms.dto.audit_logs;

import org.flexitech.projects.icpms.common.enums.VehicleAlertType;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.audit_logs.VehicleAlertLog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleAlertLogDTO extends CommonDTO {

	private Integer alertType;
	private String alertTypeDesc;
	private String plateNumber;
	private Long vehicleId;
	private Long memberId;
	private Long sessionId;
	private Long gateId;
	private Long operatorId;
	private String message;
	private Integer status;

	public VehicleAlertLogDTO(VehicleAlertLog alertLog) {
		super(alertLog);
		this.alertType = alertLog.getAlertType();
		this.alertTypeDesc = VehicleAlertType.getDescByCode(alertType);
		this.plateNumber = alertLog.getPlateNumber();
		this.vehicleId = alertLog.getVehicleId();
		this.memberId = alertLog.getMemberId();
		this.sessionId = alertLog.getSessionId();
		this.gateId = alertLog.getGateId();
		this.operatorId = alertLog.getOperatorId();
		this.message = alertLog.getMessage();
		this.status = alertLog.getStatus();
	}
}