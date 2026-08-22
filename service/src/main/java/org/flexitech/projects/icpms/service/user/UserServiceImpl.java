package org.flexitech.projects.icpms.service.user;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.user.UserDTO;
import org.flexitech.projects.icpms.dto.user.UserSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.role.Role;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.role.RoleRepository;
import org.flexitech.projects.icpms.persistence.repositories.user.UserRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.user.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationService authenticationService;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			RoleRepository roleRepository, AuthenticationService authenticationService) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationService = authenticationService;
	}

	@Override
	public UserDTO manageUser(UserDTO userDTO) throws Exception {
		User user = null;
		User loggedUser = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(userDTO.getId())) {
			user = this.userRepository.findById(userDTO.getId()).orElseThrow(() -> new Exception("User not found!"));
			user.setUpdatedTime(new Date());
			user.setCreatedBy(loggedUser);
		} else {
			user = new User();
			user.setCreatedTime(new Date());
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
			user.setUpdatedBy(loggedUser);
		}

		user.setName(userDTO.getName());
		user.setPhoneNumber(userDTO.getPhoneNumber());
		user.setStatus(userDTO.getStatus());

		if (CommonValidators.validLong(userDTO.getRoleId())) {
			Optional<Role> role = this.roleRepository.findById(userDTO.getRoleId());
			if (role.isPresent()) {
				user.setRole(role.get());
			}
		}

		User saved = this.userRepository.save(user);

		return new UserDTO(saved);
	}

	@Override
	public UserDTO getUserById(Long id) throws Exception {
		User user = this.userRepository.findById(id)
				.orElseThrow(()-> new Exception("User not found!"));
		return new UserDTO(user);
	}

	@Override
	public SearchResultDTO<UserDTO> searchUsers(UserSearchDTO searchDTO, Pageable pageable) throws Exception {
		try {
			Specification<User> spec = UserSpecification.withSearchCriteria(searchDTO);

			Page<User> userPage = userRepository.findAll(spec, pageable);

			return convertToCommonSearchDTO(userPage);
		} catch (Exception e) {
			throw new Exception("Error searching users: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean deleteUser(Long id) throws Exception {
		User user = this.userRepository.findById(id)
				.orElseThrow(()-> new Exception("User not found!"));
		this.userRepository.delete(user);
		return false;
	}

	private SearchResultDTO<UserDTO> convertToCommonSearchDTO(Page<User> userPage) {
		SearchResultDTO<UserDTO> result = new SearchResultDTO<>();

		result.setPageNo(userPage.getNumber());
		result.setLimit(userPage.getSize());
		result.setTotalPage(userPage.getTotalPages());
		result.setTotalRecords((int) userPage.getTotalElements());
		result.setPageCount(userPage.getNumberOfElements());
		result.setHasNextPage(userPage.hasNext());

		List<UserDTO> userDTOs = userPage.getContent().stream().map(UserDTO::new).collect(Collectors.toList());

		result.setResults(userDTOs);
		return result;
	}

}
