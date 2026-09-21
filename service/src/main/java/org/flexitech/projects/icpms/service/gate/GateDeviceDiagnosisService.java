package org.flexitech.projects.icpms.service.gate;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDiagnosisDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDiagnosisSearchDTO;

public interface GateDeviceDiagnosisService {

	GateDeviceDiagnosisDTO logDiagnosis(GateDeviceDiagnosisDTO dto);
	
	SearchResultDTO<GateDeviceDiagnosisDTO> searchDiagnosis(GateDeviceDiagnosisSearchDTO searchDto);
	
}
