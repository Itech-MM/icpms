package org.flexitech.projects.icpms.dto.api.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequestDTO {
	private Boolean endShift;
	private String remark;
}