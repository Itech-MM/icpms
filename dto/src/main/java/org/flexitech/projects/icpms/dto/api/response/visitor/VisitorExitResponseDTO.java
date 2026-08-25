package org.flexitech.projects.icpms.dto.api.response.visitor;

import org.flexitech.projects.icpms.dto.payment.PaymentDTO;
import org.flexitech.projects.icpms.dto.session.ParkingSessionDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitorExitResponseDTO {

	private ParkingSessionDTO session;
	private PaymentDTO payment;
}