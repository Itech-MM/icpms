package org.flexitech.projects.icpms.dto.api.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupervisorValidationResponseDTO {

	private Long supervisorId;
	private String supervisorName;
}