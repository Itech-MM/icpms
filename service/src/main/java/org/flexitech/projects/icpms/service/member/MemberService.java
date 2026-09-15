package org.flexitech.projects.icpms.service.member;

import java.util.List;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.member.MemberSearchDTO;
import org.flexitech.projects.icpms.dto.member.MembersSummaryDTO;
import org.flexitech.projects.icpms.dto.vehicle.VehicleDTO;
import org.springframework.data.domain.Pageable;

public interface MemberService {
	MemberDTO manageMember(MemberDTO dto, List<VehicleDTO> vehicles) throws Exception;
	MemberDTO getMemberById(Long id) throws Exception;
	SearchResultDTO<MemberDTO> searchMembers(MemberSearchDTO searchDTO, Pageable pageable) throws Exception;
	List<MemberDTO> findAllActiveMembers();
	boolean deleteMember(Long id) throws Exception;
	
	MembersSummaryDTO getMembersSummary();
}
