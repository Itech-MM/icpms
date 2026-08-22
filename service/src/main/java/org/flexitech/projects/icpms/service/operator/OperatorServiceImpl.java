package org.flexitech.projects.icpms.service.operator;

import java.util.Date;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.entities.site.Site;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorRepository;
import org.flexitech.projects.icpms.persistence.repositories.site.SiteRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.operator.OperatorSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OperatorServiceImpl implements OperatorService {

	private final OperatorRepository operatorRepository;
	private final SiteRepository siteRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationService authenticationService;

	public OperatorServiceImpl(OperatorRepository operatorRepository, SiteRepository siteRepository,
			PasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
		this.operatorRepository = operatorRepository;
		this.siteRepository = siteRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationService = authenticationService;
	}

	@Override
	public OperatorDTO manageOperator(OperatorDTO dto) throws Exception {
		Operator operator;
		User user = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(dto.getId())) {
			operator = this.operatorRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
			operator.setUpdatedTime(new Date());
			operator.setUpdatedBy(user);
			if (CommonValidators.validString(dto.getPassword())) {
				operator.setPassword(passwordEncoder.encode(dto.getPassword()));
			}
		} else {
			operator = new Operator();
			operator.setCreatedTime(new Date());
			operator.setCreatedBy(user);
			operator.setPassword(passwordEncoder.encode(
					CommonValidators.validString(dto.getPassword()) ? dto.getPassword() : "changeme123"));
		}
		operator.setName(dto.getName());
		operator.setUsername(dto.getUsername());
		operator.setPhoneNumber(dto.getPhoneNumber());
		operator.setRole(dto.getRole());
		operator.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		if (CommonValidators.validLong(dto.getSiteId())) {
			Site site = this.siteRepository.findById(dto.getSiteId())
					.orElseThrow(() -> new EntityNotFoundException("Site doesn't exist!"));
			operator.setSite(site);
		} else {
			operator.setSite(null);
		}

		Operator saved = this.operatorRepository.save(operator);
		return new OperatorDTO(saved);
	}

	@Override
	public OperatorDTO getOperatorById(Long id) throws Exception {
		Operator operator = this.operatorRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
		return new OperatorDTO(operator);
	}

	@Override
	public SearchResultDTO<OperatorDTO> searchOperators(OperatorSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<Operator> spec = OperatorSpecification.withSearchCriteria(searchDTO);
		Page<Operator> page = operatorRepository.findAll(spec, pageable);

		SearchResultDTO<OperatorDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(OperatorDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public boolean deleteOperator(Long id) throws Exception {
		Operator operator = this.operatorRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Operator doesn't exist!"));
		this.operatorRepository.delete(operator);
		return true;
	}
}
