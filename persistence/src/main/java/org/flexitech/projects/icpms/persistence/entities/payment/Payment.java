package org.flexitech.projects.icpms.persistence.entities.payment;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.PAYMENT_TBL)
@Getter
@Setter
public class Payment extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "session_id")
	private ParkingSession session;

	private BigDecimal amount;

	/** PaymentMethod enum code: 1=Cash, 2=Card, 3=E-Wallet, 4=Online */
	private Integer method;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "payment_time")
	private Date paymentTime;

	@Column(name = "reference_no")
	private String referenceNo;

	/** PaymentStatus enum code: 1=Paid, 2=Pending, 3=Unpaid */
	private Integer status = 2;
}
