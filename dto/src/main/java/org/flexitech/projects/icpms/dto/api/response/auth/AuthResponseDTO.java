package org.flexitech.projects.icpms.dto.api.response.auth;

import java.util.List;

import org.flexitech.projects.icpms.dto.operator.OperatorShiftDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO {

	private String accessToken;
	private String refreshToken;
	private String username;
	private List<String> roles;
	private long expiresIn;
	private boolean startShift;
	private OperatorShiftDTO activeShift;
}