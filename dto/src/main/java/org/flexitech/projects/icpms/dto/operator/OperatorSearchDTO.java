package org.flexitech.projects.icpms.dto.operator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OperatorSearchDTO {
	private String name;
	private String username;
	private Long siteId;
	private Integer status;
}
