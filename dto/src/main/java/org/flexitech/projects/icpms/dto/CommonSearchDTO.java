package org.flexitech.projects.icpms.dto;

import org.flexitech.projects.icpms.common.CommonConstants;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommonSearchDTO {
	private Integer pageNo;
	private Integer limit = CommonConstants.ROW_PER_PAGE;
	private Integer status;
}
