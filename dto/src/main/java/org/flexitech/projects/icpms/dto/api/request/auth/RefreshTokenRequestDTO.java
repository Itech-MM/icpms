package org.flexitech.projects.icpms.dto.api.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDTO {

	@NotBlank(message = "Refresh token is required")
	private String refreshToken;
}