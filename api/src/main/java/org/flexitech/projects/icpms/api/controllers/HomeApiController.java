package org.flexitech.projects.icpms.api.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.api.response.home.HomePreloadResponse;
import org.flexitech.projects.icpms.service.client.home.HomeScreenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Slf4j
public class HomeApiController {
	
	private final HomeScreenService homeScreenService;
	
	@GetMapping("/preload")
	public ResponseEntity<ApiResponse<HomePreloadResponse>> getHomePreloadData(HttpServletRequest request){
		
		HomePreloadResponse response = new HomePreloadResponse();
		
		try {
			response = homeScreenService.getHomePreloadData(request);
		}catch (Exception e) {
			log.error("Error on getting home screen preload:: {}", ExceptionUtils.getStackTrace(e));
			return ApiResponse.internalError(e.getMessage());
		}
		
		return ApiResponse.ok(response);
	}
	
}
