package org.flexitech.projects.icpms.persistence.entities.session;

import java.math.BigDecimal;
import java.util.Date;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.gate.Gate;
import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;
import org.flexitech.projects.icpms.persistence.entities.tariff.Tariff;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;

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
@Table(name = TableNames.PARKING_SESSION_TBL)
@Getter
@Setter
public class ParkingSession extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	@ManyToOne
	@JoinColumn(name = "entry_gate_id")
	private Gate entryGate;

	@ManyToOne
	@JoinColumn(name = "exit_gate_id")
	private Gate exitGate;

	@ManyToOne
	@JoinColumn(name = "parking_slot_id")
	private ParkingSlot parkingSlot;

	@ManyToOne
	@JoinColumn(name = "tariff_id")
	private Tariff tariff;

	@ManyToOne
	@JoinColumn(name = "operator_id")
	private Operator operator;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "entry_time")
	private Date entryTime;

	@Column(name = "entry_photo_url")
	private String entryPhotoUrl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "exit_time")
	private Date exitTime;

	@Column(name = "exit_photo_url")
	private String exitPhotoUrl;

	@Column(name = "total_amount")
	private BigDecimal totalAmount;

	/** ParkingSessionStatus enum code: 1=Active, 2=Completed, 3=Cancelled */
	private Integer status = 1;
}
