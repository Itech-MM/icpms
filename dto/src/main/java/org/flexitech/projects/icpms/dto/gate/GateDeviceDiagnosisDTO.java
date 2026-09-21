package org.flexitech.projects.icpms.dto.gate;

import java.time.LocalDateTime;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.DeviceHealthStatus;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.GateDeviceDiagnosis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GateDeviceDiagnosisDTO extends CommonDTO {

	private Long gateDeviceId;

	private String gateDeviceName;

	private Integer healthStatus = 0;

	private String healthStatusDesc;

	private Integer latencyMs;

	private LocalDateTime checkedAt;

	private String checkedAtDesc;

	private String statusNote;

	private String deviceResponse;

	public GateDeviceDiagnosisDTO(GateDeviceDiagnosis d) {
		super(d);

		if (d.getGateDevice() != null) {
			this.gateDeviceId = d.getGateDevice().getId();
			this.gateDeviceName = d.getGateDevice().getName();
		}

		this.healthStatus = d.getHealthStatus();
		this.healthStatusDesc = DeviceHealthStatus.getDescByCode(healthStatus);

		this.latencyMs = d.getLatencyMs();
		this.checkedAt = d.getCheckedAt();
		this.checkedAtDesc = d.getCheckedAt() != null
				? DateUtils.localDateTimeToString(d.getCheckedAt(), CommonConstants.STANDARD_24_HOUR_DATE_FORMAT2)
				: "";

		this.statusNote = d.getStatusNote();
		this.deviceResponse = d.getDeviceResponse();

	}

}
