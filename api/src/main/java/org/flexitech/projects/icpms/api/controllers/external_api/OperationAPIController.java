package org.flexitech.projects.icpms.api.controllers.external_api;

import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external-api/operation")
public class OperationAPIController {

	@Value("${app.operation.license-key}")
	private String licenseKey;

	@GetMapping("/installation/validate")
	public ResponseEntity<ApiResponse<Boolean>> validateOperationInstallation(@RequestParam String key) {

		boolean success = key != null && key.equals(licenseKey);

		return ApiResponse.ok(success, success ? "Validation success." : "Validation failed.");
	}

}
