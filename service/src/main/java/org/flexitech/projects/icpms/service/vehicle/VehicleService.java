package org.flexitech.projects.icpms.service.vehicle;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleSearchDTO;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
	VehicleDTO manageVehicle(VehicleDTO dto) throws Exception;
	VehicleDTO getVehicleById(Long id) throws Exception;
	SearchResultDTO<VehicleDTO> searchVehicles(VehicleSearchDTO searchDTO, Pageable pageable) throws Exception;
	boolean deleteVehicle(Long id) throws Exception;
}
