package org.flexitech.projects.icpms.dto.operator.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorAuthRequestDTO {

	private Integer method;
	private Long gateId;

	// PASSWORD
	private String username;
	private String password;

	// PIN
	private Long operatorId;
	private String pin;

	// RFID
	private String cardUid;
	private String blockData;

	private String qrToken;

	private String swipeData;
}