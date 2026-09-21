package org.flexitech.projects.icpms.dto.gate;

import org.flexitech.projects.icpms.dto.CommonSearchDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GateDeviceDiagnosisSearchDTO extends CommonSearchDTO{

	private Long gateDeviceId;
	
	private String fromDate;
	
	private String toDate;
	
}
