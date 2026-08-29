package org.flexitech.projects.icpms.service.session;

import java.util.Optional;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorShiftSummaryDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCloseDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionCreateDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionSearchDTO;
import org.springframework.data.domain.Pageable;

public interface ParkingSessionService {
	SearchResultDTO<ParkingSessionDTO> searchSessions(ParkingSessionSearchDTO searchDTO, Pageable pageable) throws Exception;
	ParkingSessionDTO getSessionById(Long id) throws Exception;
	long countActiveSessions();

	Optional<ParkingSessionDTO> findActiveByPlateNumber(String plateNumber);

	ParkingSessionDTO createEntry(ParkingSessionCreateDTO createDTO) throws Exception;

    ParkingSessionDTO closeSession(ParkingSessionCloseDTO closeDTO) throws Exception;

    long getElapsedMinutes(Long sessionId) throws Exception;
    

	OperatorShiftSummaryDTO getShiftSummary(Long shiftId);
}