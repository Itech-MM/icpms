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
	
	void changePassword(Long operatorId, String newPassword, String confirmPassword) throws Exception;

	void changePin(Long operatorId, String newPin, String confirmPin) throws Exception;
	boolean existsByUsername(String username);
	boolean existsByUsernameAndIdNot(String username, Long id);
	boolean existsByPinPassword(String pinPassword);
}
