package org.flexitech.projects.icpms.dto.gate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GateSearchDTO {
	private String name;
	private Long siteId;
	private Integer status;
}
