package org.flexitech.projects.icpms.service.member;

import org.flexitech.projects.icpms.dto.SearchResultDTO;
import org.flexitech.projects.icpms.dto.member.MemberDTO;
import org.flexitech.projects.icpms.dto.member.MemberSearchDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberService {
	MemberDTO manageMember(MemberDTO dto) throws Exception;
	MemberDTO getMemberById(Long id) throws Exception;
	SearchResultDTO<MemberDTO> searchMembers(MemberSearchDTO searchDTO, Pageable pageable) throws Exception;
	List<MemberDTO> findAllActiveMembers();
	boolean deleteMember(Long id) throws Exception;
}
