package org.flexitech.projects.icpms.api.controllers;

import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.vehicle.VehicleDetailResponse;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.service.member.MemberService;
import org.flexitech.projects.icpms.service.vehicle.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
@Slf4j
public class VehicleApiController {

	private final VehicleService vehicleService;
	
	private final MemberService memberService;
	
	@GetMapping("/get-by-number")
	public ResponseEntity<ApiResponse<VehicleDetailResponse>> getVehicleDetalByPlateNumber(@RequestParam String number){
		try {
			Optional<VehicleDTO> vehicle = this.vehicleService.findByPlateNumber(number);
			
			if(vehicle.isEmpty()) {
				return ApiResponse.notFound("No vehicle found with this number.");
			}
			
			VehicleDTO vehicleData = vehicle.get();
			MemberDTO member = null;
			if(CommonValidators.validLong(vehicleData.getMemberId())) {
				member = this.memberService.getMemberById(vehicleData.getMemberId());
			}
			
			return ApiResponse.ok(new VehicleDetailResponse(vehicleData, member), "Get vehicle data success.");
		}catch (Exception e) {
			log.error("Error on get vehicle detial by number:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
	}
}
