package org.flexitech.projects.icpms.persistence.entities.gate;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.GATE_DEVICE_TBL)
@Getter
@Setter
public class GateDevice extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "gate_id")
	private Gate gate;

	@Column(name = "device_type")
	private Integer deviceType;

	private String name;

	private Integer direction;

	@Column(name = "ip_address")
	private String ipAddress;

	private Integer port;

	private String username;

	private String password;

	private String model;

	private Integer status = 1;

	@Column(length = 500)
	private String remarks;
}