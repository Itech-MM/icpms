package org.flexitech.projects.icpms.dto.api.response.auth;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenValidationResponseDTO {

	private boolean valid;
	private String username;
	private List<String> roles;
	private String reason;
	private boolean startShift;

	public TokenValidationResponseDTO(boolean valid, String username, List<String> roles, String reason, boolean startShift) {
		this.valid = valid;
		this.username = username;
		this.roles = roles;
		this.reason = reason;
		this.startShift = startShift;
	}
}