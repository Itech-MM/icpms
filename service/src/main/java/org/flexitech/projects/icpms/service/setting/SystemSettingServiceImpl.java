package org.flexitech.projects.icpms.service.setting;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.InputType;
import org.flexitech.projects.icpms.common.exceptions.SystemSettingNotFoundException;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.setting.SystemSetting;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.setting.SystemSettingRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.setting.SystemSettingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

	private final SystemSettingRepository systemSettingRepository;
	private final AuthenticationService authenticationService;

	@Override
	public SystemSettingDTO manageSystemSetting(SystemSettingDTO dto) throws SystemSettingNotFoundException {
		User loggedUser = authenticationService.getLoggedInUser();
		Date date = new Date();
		SystemSetting setting = null;
		if (CommonValidators.validLong(dto.getId())) {
			setting = this.systemSettingRepository.findById(dto.getId()).orElseThrow(
					() -> new SystemSettingNotFoundException("System setting not found with id " + dto.getId()));
			setting.setUpdatedBy(loggedUser);
			setting.setUpdatedTime(date);
		} else {
			setting = new SystemSetting();
			setting.setCreatedBy(loggedUser);
			setting.setCreatedTime(date);
		}
		setting.setCode(dto.getCode());
		setting.setValue(dto.getValue());
		setting.setDescription(dto.getDescription());
		setting.setEditableStatus(dto.getEditableStatus());
		setting.setSequence(dto.getSequence());

		setting = this.systemSettingRepository.save(setting);
		return new SystemSettingDTO(setting);
	}

	@Override
	public SearchResultDTO<SystemSettingDTO> searchSystemSetting(SystemSettingSearchDTO searchDTO) {
		int pageNo = (searchDTO.getPageNo() != null && searchDTO.getPageNo() > 0) ? searchDTO.getPageNo() - 1 : 0;
		int limit = (searchDTO.getLimit() != null && searchDTO.getLimit() > 0) ? searchDTO.getLimit()
				: CommonConstants.ROW_PER_PAGE;
		Pageable pageable = PageRequest.of(pageNo, limit, Sort.by("sequence").ascending());

		Specification<SystemSetting> specification = SystemSettingSpecification.withSearchCriteria(searchDTO);

		Page<SystemSetting> userPage = this.systemSettingRepository.findAll(specification, pageable);

		SearchResultDTO<SystemSettingDTO> result = new SearchResultDTO<>();

		result.setPageNo(userPage.getNumber());
		result.setLimit(userPage.getSize());
		result.setTotalPage(userPage.getTotalPages());
		result.setTotalRecords((int) userPage.getTotalElements());
		result.setPageCount(userPage.getNumberOfElements());
		result.setHasNextPage(userPage.hasNext());

		List<SystemSettingDTO> userDTOs = userPage.getContent().stream().map(SystemSettingDTO::new)
				.collect(Collectors.toList());

		result.setResults(userDTOs);

		return result;
	}

	@Override
	public SystemSettingDTO getByCode(String code) throws SystemSettingNotFoundException {
		SystemSetting setting = this.systemSettingRepository.findByCode(code)
				.orElseThrow(() -> new SystemSettingNotFoundException("System setting not found with code " + code));
		return new SystemSettingDTO(setting);
	}

	@Override
	public List<SystemSettingDTO> batchUpdateSystemSettings(List<SystemSettingDTO> settings)
	        throws SystemSettingNotFoundException {

	    if (!CommonValidators.validList(settings)) return Collections.emptyList();

	    List<SystemSetting> entities = new ArrayList<>();
	    User logged = authenticationService.getLoggedInUser();
	    Date now = new Date();

	    for (SystemSettingDTO setting : settings) {
	        if (!CommonValidators.validLong(setting.getId())) continue;

	        SystemSetting entity = this.systemSettingRepository.findById(setting.getId())
	                .orElseThrow(() -> new SystemSettingNotFoundException("System setting not found!"));

	        if (entity.getEditableStatus() != null && entity.getEditableStatus() == 0) {
	            continue;
	        }

	        String normalizedValue = normalizeValueByInputType(entity.getInputType(), setting.getValue(), entity.getCode());

	        if (InputType.PASSWORD.getCode().equals(entity.getInputType()) && !CommonValidators.validString(normalizedValue)) {
	            continue;
	        }

	        if (!Objects.equals(entity.getValue(), normalizedValue)) {
	            entity.setUpdatedBy(logged);
	            entity.setUpdatedTime(now);
	            entity.setValue(normalizedValue);
	            entities.add(entity);
	        }
	    }

	    List<SystemSetting> saved = systemSettingRepository.saveAll(entities);

	    return saved.stream().map(SystemSettingDTO::new).toList();
	}

	private String normalizeValueByInputType(Integer inputTypeCode, String rawValue, String settingCode) {
	    if (inputTypeCode == null) {
	        return rawValue == null ? null : rawValue.trim();
	    }

	    InputType type = Arrays.stream(InputType.values())
	            .filter(t -> t.getCode().equals(inputTypeCode))
	            .findFirst()
	            .orElse(null);

	    if (type == null) {
	        return rawValue == null ? null : rawValue.trim();
	    }

	    switch (type) {

	        case NUMBER: {
	            if (!CommonValidators.validString(rawValue) || !rawValue.trim().matches("^-?\\d+(\\.\\d+)?$")) {
	                throw new IllegalArgumentException("Invalid number value for setting: " + settingCode);
	            }
	            return rawValue.trim();
	        }

	        case TOGGLE: {
	            String v = rawValue == null ? "" : rawValue.trim().toLowerCase();
	            boolean isTrue = v.equals("true") || v.equals("1") || v.equals("on") || v.equals("yes");
	            return isTrue ? "1" : "0";
	        }

	        case DATE: {
	            if (!CommonValidators.validString(rawValue)) {
	                throw new IllegalArgumentException("Invalid date value for setting: " + settingCode);
	            }
	            try {
	                LocalDate.parse(rawValue.trim());
	            } catch (DateTimeParseException e) {
	                throw new IllegalArgumentException("Invalid date value for setting: " + settingCode);
	            }
	            return rawValue.trim();
	        }

	        case PASSWORD: {
	            return rawValue == null ? null : rawValue.trim();
	        }

	        case TEXT:
	        default: {
	            return rawValue == null ? null : rawValue.trim();
	        }
	    }
	}

	@Override
	public List<SystemSettingDTO> getAllOperatorSettings() {
		return this.systemSettingRepository.findBySyncToOperatorStatus(ActiveStatus.ACTIVE.getCode())
				.stream().map(SystemSettingDTO::new).collect(Collectors.toList());
	}

}