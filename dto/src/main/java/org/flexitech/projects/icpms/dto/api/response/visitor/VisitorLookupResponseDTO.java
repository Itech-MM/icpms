package org.flexitech.projects.icpms.dto.api.response.visitor;

import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorLookupResponseDTO {

	private boolean hasActiveSession;
	private ParkingSessionDTO activeSession;
	private boolean member;
	private String memberName;
	private boolean vip;
}