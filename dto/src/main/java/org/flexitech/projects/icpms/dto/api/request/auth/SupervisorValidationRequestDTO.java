package org.flexitech.projects.icpms.dto.api.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupervisorValidationRequestDTO {

	@NotBlank
	private String username;

	@NotBlank
	private String password;
}