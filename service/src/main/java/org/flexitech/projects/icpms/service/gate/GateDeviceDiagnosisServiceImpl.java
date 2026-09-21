package org.flexitech.projects.icpms.service.gate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.utils.CommonUtils;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDiagnosisDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDiagnosisSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.GateDevice;
import org.flexitech.projects.icpms.persistence.entities.gate.GateDeviceDiagnosis;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateDeviceDiagnosisRepository;
import org.flexitech.projects.icpms.persistence.repositories.gate.GateDeviceRepository;
import org.flexitech.projects.icpms.service.specifications.gate.GateDeviceDiagnosisSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GateDeviceDiagnosisServiceImpl implements GateDeviceDiagnosisService{
	
	private final GateDeviceDiagnosisRepository repository;
	private final GateDeviceRepository gateDeviceRepository;
	
	@Override
	public GateDeviceDiagnosisDTO logDiagnosis(GateDeviceDiagnosisDTO dto) {
		
		GateDevice device = this.gateDeviceRepository.findById(dto.getGateDeviceId())
				.orElseThrow(()-> new RuntimeException("Invalid device!"));
		
		GateDeviceDiagnosis d = new GateDeviceDiagnosis();
		d.setGateDevice(device);
		d.setCheckedAt(dto.getCheckedAt());
		d.setHealthStatus(dto.getHealthStatus());
		d.setCreatedTime(new Date());
		d.setDeviceResponse(dto.getDeviceResponse());
		d.setHealthStatus(dto.getHealthStatus());
		d.setLatencyMs(dto.getLatencyMs());
		d.setStatusNote(dto.getStatusNote());

		GateDeviceDiagnosis saved = this.repository.save(d);
		
		return new GateDeviceDiagnosisDTO(saved);
	}

	@Override
	public SearchResultDTO<GateDeviceDiagnosisDTO> searchDiagnosis(GateDeviceDiagnosisSearchDTO searchDTO) {
		Specification<GateDeviceDiagnosis> spec = GateDeviceDiagnosisSpecification.withSearchCriteria(searchDTO);
		
		Pageable pageable = PageRequest.of(CommonUtils.getDefaultValue(searchDTO.getPageNo(), 1) - 1,
				CommonUtils.getDefaultValue(searchDTO.getLimit(), CommonConstants.ROW_PER_PAGE), 
				Sort.by("checkedAt").descending());
		
		Page<GateDeviceDiagnosis> page = repository.findAll(spec, pageable);

		SearchResultDTO<GateDeviceDiagnosisDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(GateDeviceDiagnosisDTO::new).collect(Collectors.toList()));
		return result;
	}

}
