package org.flexitech.projects.icpms.service.vehicle;

import java.util.Date;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleSearchDTO;
import org.flexitech.projects.icpms.persistence.entities.member.Member;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberRepository;
import org.flexitech.projects.icpms.persistence.repositories.vehicle.VehicleRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.vehicle.VehicleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VehicleServiceImpl implements VehicleService {

	private final VehicleRepository vehicleRepository;
	private final MemberRepository memberRepository;
	private final AuthenticationService authenticationService;

	public VehicleServiceImpl(VehicleRepository vehicleRepository, MemberRepository memberRepository,
			AuthenticationService authenticationService) {
		this.vehicleRepository = vehicleRepository;
		this.memberRepository = memberRepository;
		this.authenticationService = authenticationService;
	}

	@Override
	public VehicleDTO manageVehicle(VehicleDTO dto) throws Exception {
		Vehicle vehicle;
		User user = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(dto.getId())) {
			vehicle = this.vehicleRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Vehicle doesn't exist!"));
			vehicle.setUpdatedTime(new Date());
			vehicle.setUpdatedBy(user);
		} else {
			vehicle = new Vehicle();
			vehicle.setCreatedTime(new Date());
			vehicle.setCreatedBy(user);
		}
		vehicle.setPlateNumber(dto.getPlateNumber());
		vehicle.setVehicleType(dto.getVehicleType());
		vehicle.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		if (CommonValidators.validLong(dto.getMemberId())) {
			Member member = this.memberRepository.findById(dto.getMemberId())
					.orElseThrow(() -> new EntityNotFoundException("Member doesn't exist!"));
			vehicle.setMember(member);
		} else {
			vehicle.setMember(null);
		}

		Vehicle saved = this.vehicleRepository.save(vehicle);
		return new VehicleDTO(saved);
	}

	@Override
	public VehicleDTO getVehicleById(Long id) throws Exception {
		Vehicle vehicle = this.vehicleRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Vehicle doesn't exist!"));
		return new VehicleDTO(vehicle);
	}

	@Override
	public SearchResultDTO<VehicleDTO> searchVehicles(VehicleSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<Vehicle> spec = VehicleSpecification.withSearchCriteria(searchDTO);
		Page<Vehicle> page = vehicleRepository.findAll(spec, pageable);

		SearchResultDTO<VehicleDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(VehicleDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public boolean deleteVehicle(Long id) throws Exception {
		Vehicle vehicle = this.vehicleRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Vehicle doesn't exist!"));
		this.vehicleRepository.delete(vehicle);
		return true;
	}
}
