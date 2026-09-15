package org.flexitech.projects.icpms.dto.operator.auth;

import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResult {

	private boolean success;
	private OperatorDTO operator;
	private String sessionToken;
	private String errorCode;

	public static AuthResult success(OperatorDTO operator, String sessionToken) {
		return new AuthResult(true, operator, sessionToken, null);
	}

	public static AuthResult failure(String errorCode) {
		return new AuthResult(false, null, null, errorCode);
	}
}