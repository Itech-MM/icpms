package org.flexitech.projects.icpms.service.session;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionSearchDTO;
import org.springframework.data.domain.Pageable;

public interface ParkingSessionService {
	SearchResultDTO<ParkingSessionDTO> searchSessions(ParkingSessionSearchDTO searchDTO, Pageable pageable) throws Exception;
	ParkingSessionDTO getSessionById(Long id) throws Exception;
	long countActiveSessions();
}
