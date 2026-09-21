package org.flexitech.projects.icpms.persistence.entities.gate;

import java.time.LocalDateTime;

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

	@Column(name = "connection_type")
	private Integer connectionType;

	private String name;

	private Integer direction;

	@Column(name = "ip_address")
	private String ipAddress;

	private Integer port;

	@Column(name = "com_port")
	private String comPort;

	@Column(name = "baud_rate")
	private Integer baudRate;

	private String username;

	private String password;

	private String model;

	@Column(name = "firmware_version")
	private String firmwareVersion;

	@Column(name = "serial_number")
	private String serialNumber;

	private Integer status = 1;

	@Column(name = "access_url")
	private String accessUrl;

	@Column(length = 500)
	private String remarks;

	@Column(name = "last_health_status")
	private Integer lastHealthStatus = 0;

	@Column(name = "last_latency_ms")
	private Integer lastLatencyMs;

	@Column(name = "last_checked_at")
	private LocalDateTime lastCheckedAt;

	@Column(name = "last_status_note", length = 255)
	private String lastStatusNote;
}