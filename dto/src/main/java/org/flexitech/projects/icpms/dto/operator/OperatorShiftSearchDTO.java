package org.flexitech.projects.icpms.dto.operator;

import org.flexitech.projects.icpms.dto.CommonSearchDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OperatorShiftSearchDTO extends CommonSearchDTO{

	private Long operatorId;
	private String shiftCode;
	private Integer shiftStatus;
	private Long gateId;
	private String fromDateTime;
	private String toDateTime;
	
}
