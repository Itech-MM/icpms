package org.flexitech.projects.icpms.service.operator;

import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;

public interface OperatorShiftService {
	OperatorShiftDTO startShift(OperatorShiftDTO dto);
	OperatorShiftDTO endShift(OperatorShiftDTO dto);
	OperatorShiftDTO getActiveShiftByOperator(Long operatorId, String gateIpAddress);
}
