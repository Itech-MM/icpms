package org.flexitech.projects.icpms.service.parking;

import java.util.List;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaRealtimeDTO;
import org.flexitech.projects.icpms.dto.parking.ParkingAreaSearchDTO;
import org.springframework.data.domain.Pageable;

public interface ParkingAreaService {

	SearchResultDTO<ParkingAreaDTO> searchParkingAreas(ParkingAreaSearchDTO searchDTO, Pageable pageable) throws Exception;

	ParkingAreaDTO getParkingAreaById(Long id) throws Exception;

	void manageParkingArea(ParkingAreaDTO dto) throws Exception;

	void deleteParkingArea(Long id) throws Exception;
	
	List<ParkingAreaDTO> findAllActiveParkingAreas();
	
	ParkingAreaRealtimeDTO getRealtimeByGate(String gateIpAddress) throws Exception;

}