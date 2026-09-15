package org.flexitech.projects.icpms.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembersSummaryDTO {

	private long totalMembers;
	private long regularMembers;
	private long vipMembers;
	private long expiredMembers;
	private long activeMembers;
}