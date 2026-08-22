package org.flexitech.projects.icpms.service.gate;

import java.util.List;

import org.flexitech.projects.icpms.dto.gate.GateDeviceDTO;

public interface GateDeviceService {
	GateDeviceDTO manageDevice(GateDeviceDTO dto) throws Exception;
	GateDeviceDTO getDeviceById(Long id) throws Exception;
	List<GateDeviceDTO> getDevicesByGate(Long gateId);
	boolean deleteDevice(Long id) throws Exception;
}