package org.flexitech.projects.icpms.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSearchDTO {
	private String name;
	private String phoneNumber;
	private Integer status;
}
