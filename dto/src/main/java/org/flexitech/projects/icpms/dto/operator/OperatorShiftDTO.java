package org.flexitech.projects.icpms.dto.operator;

import java.math.BigDecimal;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.enums.ShiftStatus;
import org.flexitech.projects.icpms.common.utils.CommonUtils;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.persistence.entities.operator.OperatorShift;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OperatorShiftDTO extends CommonDTO {
	private String code;
	private OperatorDTO operator;
	private Long operatorId;

	private GateDTO gate;
	private Long gateId;
	private String gateIpAddress;

	private String startDateTime;

	private String endDateTime;

	private Integer shiftStatus;

	private String shiftStatusDesc;

	private BigDecimal openingCash;

	private String openingCashDesc;

	private BigDecimal closingCash;

	private String closingCashDesc;

	private BigDecimal diff;

	private String diffDesc;

	private String remark;

	public OperatorShiftDTO(OperatorShift o) {
		super(o);

		this.code = o.getCode();
		
		if (o.getOperator() != null) {
			this.operator = new OperatorDTO(o.getOperator());
			this.operatorId = o.getOperator().getId();
		}

		if (o.getGate() != null) {
			this.gate = new GateDTO(o.getGate());
			this.gateId = o.getGate().getId();
			this.gateIpAddress = o.getGate().getGateIpAddress();
		}

		if (o.getStartDateTime() != null) {
			this.startDateTime = DateUtils.dateToString(o.getStartDateTime(),
					CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}

		if (o.getEndDateTime() != null) {
			this.endDateTime = DateUtils.dateToString(o.getEndDateTime(),
					CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}

		this.shiftStatus = o.getShiftStatus();
		this.shiftStatusDesc = ShiftStatus.getDescByCode(shiftStatus);

		this.openingCash = CommonUtils.getDefaultValue(o.getOpeningCash(), BigDecimal.ZERO);
		this.openingCashDesc = CommonUtils.formatNumber(openingCash);

		this.closingCash = CommonUtils.getDefaultValue(o.getClosingCash(), BigDecimal.ZERO);
		this.closingCashDesc = CommonUtils.formatNumber(closingCash);

		this.diff = CommonUtils.getDefaultValue(o.getDiff(), BigDecimal.ZERO);
		this.diffDesc = CommonUtils.formatNumber(diff);

		this.remark = o.getRemark();
	}

}
