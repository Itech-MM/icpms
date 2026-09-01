package org.flexitech.projects.icpms.dto.api.response.vehicle;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleDetailResponse {

	private VehicleDTO vehicle;
	
	private MemberDTO memeber;
	
	@JsonProperty("isMember")
	private Boolean isMember = false;
	
	@JsonProperty("isVip")
	private Boolean isVip = false;
	
	public VehicleDetailResponse(VehicleDTO vehicle, MemberDTO member) {
		if(CommonValidators.isValidObject(vehicle)) {
			this.vehicle =vehicle;
		}
		
		if(CommonValidators.isValidObject(member)) {
			isMember = true;
			this.memeber = member;
			isVip = member.getIsVip();
		}
		
	}
	
}
