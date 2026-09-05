package org.flexitech.projects.icpms.dto.audit_logs;

import org.flexitech.projects.icpms.dto.CommonSearchDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VehicleAlertLogSearchDTO extends CommonSearchDTO {
	private String startDate;
	private String endDate;
	private Long sessionId;
	private Long gateId;
}
