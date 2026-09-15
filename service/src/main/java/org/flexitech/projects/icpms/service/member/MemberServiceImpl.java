package org.flexitech.projects.icpms.service.member;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.BlacklistStatus;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.member.MemberSearchDTO;
import org.flexitech.projects.icpms.dto.member.MembersSummaryDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.flexitech.projects.icpms.persistence.entities.member.Member;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;
import org.flexitech.projects.icpms.persistence.repositories.member.MemberRepository;
import org.flexitech.projects.icpms.persistence.repositories.slot.ParkingSlotRepository;
import org.flexitech.projects.icpms.persistence.repositories.vehicle.VehicleRepository;
import org.flexitech.projects.icpms.service.auth.AuthenticationService;
import org.flexitech.projects.icpms.service.specifications.member.MemberSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final ParkingSlotRepository slotRepository;
	private final AuthenticationService authenticationService;
	private final VehicleRepository vehicleRepository;

	public MemberServiceImpl(MemberRepository memberRepository, ParkingSlotRepository slotRepository,
			AuthenticationService authenticationService, VehicleRepository vehicleRepository) {
		this.memberRepository = memberRepository;
		this.slotRepository = slotRepository;
		this.authenticationService = authenticationService;
		this.vehicleRepository = vehicleRepository;
	}

	@Override
	@Transactional
	public MemberDTO manageMember(MemberDTO dto, List<VehicleDTO> vehicles) throws Exception {
		Member member;
		User user = this.authenticationService.getLoggedInUser();
		if (CommonValidators.validLong(dto.getId())) {
			member = this.memberRepository.findById(dto.getId())
					.orElseThrow(() -> new EntityNotFoundException("Member doesn't exist!"));
			member.setUpdatedTime(new Date());
			member.setUpdatedBy(user);
		} else {
			member = new Member();
			member.setCreatedTime(new Date());
			member.setCreatedBy(user);
		}
		member.setName(dto.getName());
		member.setPhoneNumber(dto.getPhoneNumber());
		member.setEmail(dto.getEmail());
		member.setMembershipType(dto.getMembershipType());
		member.setIsVip(dto.getIsVip() != null && dto.getIsVip());
		member.setStatus(CommonValidators.isValidObject(dto.getStatus()) ? dto.getStatus() : ActiveStatus.ACTIVE.getCode());

		if (CommonValidators.validString(dto.getValidUntil())) {
			member.setValidUntil(DateUtils.stringToDate(dto.getValidUntil(), CommonConstants.STANDARD_DB_DATE_FORMAT));
		} else {
			member.setValidUntil(null);
		}

		if (CommonValidators.validLong(dto.getReservedSlotId())) {
			ParkingSlot slot = this.slotRepository.findById(dto.getReservedSlotId())
					.orElseThrow(() -> new EntityNotFoundException("Parking slot doesn't exist!"));
			member.setReservedSlot(slot);
		} else {
			member.setReservedSlot(null);
		}

		Member saved = this.memberRepository.save(member);

		if (CommonValidators.validList(vehicles)) {
			linkVehiclesToMember(saved, vehicles, user);
		}

		return new MemberDTO(saved);
	}

	private void linkVehiclesToMember(Member member, List<VehicleDTO> vehicles, User user) {
		for (VehicleDTO vehicleDTO : vehicles) {
			if (!CommonValidators.validString(vehicleDTO.getPlateNumber())) {
				continue;
			}

			Optional<Vehicle> existing = this.vehicleRepository.findByPlateNumberIgnoreCase(vehicleDTO.getPlateNumber());
			Vehicle vehicle;
			if (existing.isPresent()) {
				vehicle = existing.get();
				vehicle.setUpdatedTime(new Date());
				vehicle.setUpdatedBy(user);
			} else {
				vehicle = new Vehicle();
				vehicle.setCreatedTime(new Date());
				vehicle.setCreatedBy(user);
				vehicle.setPlateNumber(vehicleDTO.getPlateNumber());
				vehicle.setStatus(ActiveStatus.ACTIVE.getCode());
				vehicle.setBlackListStatus(BlacklistStatus.WHITELIST.getCode());
			}

			if (CommonValidators.validString(vehicleDTO.getVehicleType())) {
				vehicle.setVehicleType(vehicleDTO.getVehicleType());
			}
			vehicle.setMember(member);

			this.vehicleRepository.save(vehicle);
		}
	}

	@Override
	public MemberDTO getMemberById(Long id) throws Exception {
		Member member = this.memberRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Member doesn't exist!"));
		return new MemberDTO(member);
	}

	@Override
	public SearchResultDTO<MemberDTO> searchMembers(MemberSearchDTO searchDTO, Pageable pageable) throws Exception {
		Specification<Member> spec = MemberSpecification.withSearchCriteria(searchDTO);
		Page<Member> page = memberRepository.findAll(spec, pageable);

		SearchResultDTO<MemberDTO> result = new SearchResultDTO<>();
		result.setPageNo(page.getNumber());
		result.setLimit(page.getSize());
		result.setTotalPage(page.getTotalPages());
		result.setTotalRecords((int) page.getTotalElements());
		result.setPageCount(page.getNumberOfElements());
		result.setHasNextPage(page.hasNext());
		result.setResults(page.getContent().stream().map(MemberDTO::new).collect(Collectors.toList()));
		return result;
	}

	@Override
	public List<MemberDTO> findAllActiveMembers() {
		MemberSearchDTO searchDTO = new MemberSearchDTO();
		searchDTO.setStatus(ActiveStatus.ACTIVE.getCode());
		Specification<Member> spec = MemberSpecification.withSearchCriteria(searchDTO);
		return this.memberRepository.findAll(spec).stream().map(MemberDTO::new).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public boolean deleteMember(Long id) throws Exception {
		Member member = this.memberRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Member doesn't exist!"));
		this.memberRepository.delete(member);
		return true;
	}
	
	@Override
	public MembersSummaryDTO getMembersSummary() {
		long total = memberRepository.count();
		long regular = memberRepository.count(MemberSpecification.isRegular());
		long vip = memberRepository.count(MemberSpecification.isVip());
		long expired = memberRepository.count(MemberSpecification.isExpired());
		long active = memberRepository.count(MemberSpecification.isActive());

		return new MembersSummaryDTO(total, regular, vip, expired, active);
	}
}
