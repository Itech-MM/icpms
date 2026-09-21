package org.flexitech.projects.icpms.admin.controller.validator;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import org.flexitech.projects.icpms.service.operator.OperatorService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperatorValidator implements Validator {

	private final OperatorService operatorService;

	@Override
	public boolean supports(Class<?> clazz) {
		return OperatorDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		OperatorDTO dto = (OperatorDTO) target;
		boolean isNew = !CommonValidators.validLong(dto.getId());

		if (CommonValidators.validString(dto.getUsername())) {
			boolean usernameTaken = isNew
					? operatorService.existsByUsername(dto.getUsername())
					: operatorService.existsByUsernameAndIdNot(dto.getUsername(), dto.getId());
			if (usernameTaken) {
				errors.rejectValue("username", "duplicate.username",
						"This username is already in use, please choose another.");
			}
		}

		if (isNew && CommonValidators.validString(dto.getPinPassword())) {
			if (operatorService.existsByPinPassword(dto.getPinPassword())) {
				errors.rejectValue("pinPassword", "duplicate.pinPassword",
						"This PIN is already in use, please choose another.");
			}
		}
	}
}