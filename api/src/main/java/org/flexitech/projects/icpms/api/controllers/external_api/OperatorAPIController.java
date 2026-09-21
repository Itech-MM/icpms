package org.flexitech.projects.icpms.api.controllers.external_api;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.api.response.ApiResponse;
import org.flexitech.projects.icpms.dto.operator.OperatorDTO;
import org.flexitech.projects.icpms.dto.operator.OperatorSearchDTO;
import org.flexitech.projects.icpms.service.operator.OperatorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/external-api/operators")
@RequiredArgsConstructor
public class OperatorAPIController {

	private final OperatorService operatorService;

	@GetMapping
	public ResponseEntity<ApiResponse<SearchResultDTO<OperatorDTO>>> search(
			OperatorSearchDTO searchDTO,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws Exception {

		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return ApiResponse.ok(operatorService.searchOperators(searchDTO, pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OperatorDTO>> getById(@PathVariable Long id) throws Exception {
		return ApiResponse.ok(operatorService.getOperatorById(id));
	}
}