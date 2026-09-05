package org.flexitech.projects.icpms.service.vehicle;

import java.util.Optional;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleSearchDTO;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
	VehicleDTO manageVehicle(VehicleDTO dto) throws Exception;
	VehicleDTO getVehicleById(Long id) throws Exception;
	SearchResultDTO<VehicleDTO> searchVehicles(VehicleSearchDTO searchDTO, Pageable pageable) throws Exception;
	boolean deleteVehicle(Long id) throws Exception;

	Optional<VehicleDTO> findByPlateNumber(String plateNumber);

	VehicleDTO findOrCreateByPlateNumber(String plateNumber, String vehicleType) throws Exception;
	
	boolean isBlacklist(String plateNumber);
}