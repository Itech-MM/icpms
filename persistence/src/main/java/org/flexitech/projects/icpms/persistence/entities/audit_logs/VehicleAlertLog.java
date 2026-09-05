package org.flexitech.projects.icpms.persistence.entities.audit_logs;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.VEHICLE_ALERT_LOG_TBL)
@Getter
@Setter
public class VehicleAlertLog extends BasedEntity {

	@Column(name = "plate_number")
	private String plateNumber;

	@Column(name = "vehicle_id")
	private Long vehicleId;

	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "session_id")
	private Long sessionId;

	@Column(name = "gate_id")
	private Long gateId;

	@Column(name = "operator_id")
	private Long operatorId;

	@Column(name = "alert_type")
	private Integer alertType;

	@Column(name = "message")
	private String message;
	
	private Integer status = 1;
}