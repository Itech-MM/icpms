package org.flexitech.projects.icpms.service.operator;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorSearchDTO;
import org.springframework.data.domain.Pageable;

public interface OperatorService {
	OperatorDTO manageOperator(OperatorDTO dto) throws Exception;
	OperatorDTO getOperatorById(Long id) throws Exception;
	SearchResultDTO<OperatorDTO> searchOperators(OperatorSearchDTO searchDTO, Pageable pageable) throws Exception;
	boolean deleteOperator(Long id) throws Exception;
}
