package org.flexitech.projects.icpms.service.session;

import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.enums.ParkingSessionStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.flexitech.projects.icpms.persistence.repositories.session.ParkingSessionRepository;
import org.flexitech.projects.icpms.service.specifications.session.ParkingSessionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ParkingSessionServiceImpl implements ParkingSessionService {

	private final ParkingSessionRepository sessionRepository;

	public ParkingSessionServiceImpl(ParkingSessionRepository sessionRepository) {
		this.sessionRepository = sessionRepository;
	}

	@Override
	public SearchResultDTO<ParkingSessionDTO> searchSessions(ParkingSessionSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<ParkingSession> spec = ParkingSessionSpecification.withSearchCriteria(searchDTO);
		Page<ParkingSession> page = sessionRepository.findAll(spec, pageable);

		SearchResultDTO<ParkingSessionDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(ParkingSessionDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public ParkingSessionDTO getSessionById(Long id) throws Exception {
		ParkingSession session = this.sessionRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Parking session doesn't exist!"));
		return new ParkingSessionDTO(session);
	}

	@Override
	public long countActiveSessions() {
		return this.sessionRepository.countByStatus(ParkingSessionStatus.ACTIVE.getCode());
	}
}
