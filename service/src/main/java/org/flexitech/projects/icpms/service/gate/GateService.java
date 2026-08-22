package org.flexitech.projects.icpms.service.gate;

import java.util.List;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.gate.GateSearchDTO;
import org.springframework.data.domain.Pageable;

public interface GateService {
	GateDTO manageGate(GateDTO dto) throws Exception;
	GateDTO getGateById(Long id) throws Exception;
	SearchResultDTO<GateDTO> searchGates(GateSearchDTO searchDTO, Pageable pageable) throws Exception;
	List<GateDTO> findBySite(Long siteId);
	List<GateDTO> findAll();
	boolean deleteGate(Long id) throws Exception;
}
