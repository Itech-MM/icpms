package org.flexitech.projects.icpms.dto.api.response.visitor;

import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorLookupResponseDTO {

	private Boolean hasActiveSession;
	private ParkingSessionDTO activeSession;
	private Boolean member;
	private String memberName;
	private Boolean vip;
	private Boolean blacklist;
}