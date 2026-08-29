package org.flexitech.projects.icpms.dto.api.request.shift;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class EndShiftRequestDTO {

	private BigDecimal closingCash;

	private String remark;
}