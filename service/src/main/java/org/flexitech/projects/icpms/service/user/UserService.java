package org.flexitech.projects.icpms.service.user;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.user.UserDTO;
import org.flexitech.projects.icpms.dto.user.UserSearchDTO;
import org.springframework.data.domain.Pageable;

public interface UserService {
	
	UserDTO manageUser(UserDTO userDTO) throws Exception;
	
	UserDTO getUserById(Long id)throws Exception;
	
	SearchResultDTO<UserDTO> searchUsers(UserSearchDTO searchDTO, Pageable pageable) throws Exception;
	
	boolean deleteUser(Long id) throws Exception;
}
