package org.flexitech.projects.icpms.service.audit_logs;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogDTO;
import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogSearchDTO;

public interface VehicleAlertLogService {

	void logUnknownPlate(String plateNumber, Long gateId, Long operatorId);

	void logBlacklistDetected(String plateNumber, Long vehicleId, Long gateId, Long operatorId);

	void logMemberExpired(String plateNumber, Long vehicleId, Long memberId, Long sessionId, Long gateId,
			Long operatorId);
	
	SearchResultDTO<VehicleAlertLogDTO> searchVehicleAlertLogs(VehicleAlertLogSearchDTO searchDTO);
	
	void updateStatus(Integer status, Long id);
}