package org.flexitech.projects.icpms.service.operator;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftSearchDTO;

public interface OperatorShiftService {
	OperatorShiftDTO startShift(OperatorShiftDTO dto);
	OperatorShiftDTO endShift(OperatorShiftDTO dto);
	OperatorShiftDTO getActiveShiftByOperator(Long operatorId, String gateIpAddress);
	
	SearchResultDTO<OperatorShiftDTO> searchOperatorShift(OperatorShiftSearchDTO searchDTO, boolean export);
	OperatorShiftDTO findById(Long id);
}
