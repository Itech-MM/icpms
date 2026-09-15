package org.flexitech.projects.icpms.dto.api.request.member;

import java.util.List;

import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterMemberRequest {

	@NotNull
	private MemberDTO member;
	
	@NotEmpty
	private List<VehicleDTO> vehicles;
	
}
