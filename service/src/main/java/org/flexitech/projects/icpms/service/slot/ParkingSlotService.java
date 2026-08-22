package org.flexitech.projects.icpms.service.slot;

import java.util.List;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotBulkDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotDTO;
import org.flexitech.projects.icpms.dto.slot.ParkingSlotSearchDTO;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

public interface ParkingSlotService {
	ParkingSlotDTO manageSlot(ParkingSlotDTO dto) throws Exception;
	ParkingSlotDTO getSlotById(Long id) throws Exception;
	SearchResultDTO<ParkingSlotDTO> searchSlots(ParkingSlotSearchDTO searchDTO, Pageable pageable) throws Exception;
	List<ParkingSlotDTO> findAvailableSlots(Long siteId);
	boolean deleteSlot(Long id) throws Exception;
	int bulkCreateSlots(@Valid ParkingSlotBulkDTO bulkDTO) throws Exception;
}
