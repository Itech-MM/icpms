package org.flexitech.projects.icpms.persistence.entities.operator;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = TableNames.OPERATOR_SHIFT_TBL)
@Getter
@Setter
@NoArgsConstructor
public class OperatorShift extends BasedEntity{
	
	private String code;

	@ManyToOne
	@JoinColumn(name = "operator_id")
	private Operator operator;
	
	@ManyToOne
	@JoinColumn(name = "gate_id")
	private Gate gate;
	
	@Column(name = "start_date_time")
	private Date startDateTime;
	
	@Column(name = "end_date_time")
	private Date endDateTime;
	
	@Column(name = "shift_status")
	private Integer shiftStatus;

	@Column(name = "opening_cash")
	private BigDecimal openingCash;
	
	@Column(name = "closing_cash")
	private BigDecimal closingCash;
	
	private BigDecimal diff;
	
	private String remark;
	
}
