package org.flexitech.projects.icpms.dto.api.request.shift;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartShiftRequestDTO {
	private BigDecimal openingCash;
	private String remark;
}