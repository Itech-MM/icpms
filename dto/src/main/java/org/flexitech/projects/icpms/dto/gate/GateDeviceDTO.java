package org.flexitech.projects.icpms.dto.gate;

import java.time.LocalDateTime;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.common.enums.DeviceConnectionType;
import org.flexitech.projects.icpms.common.enums.DeviceHealthStatus;
import org.flexitech.projects.icpms.common.enums.GateDeviceType;
import org.flexitech.projects.icpms.common.enums.GateType;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.gate.GateDevice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GateDeviceDTO extends CommonDTO {

	@NotNull
	private Long gateId;
	private String gateName;

	@NotNull
	private Integer deviceType;
	private String deviceTypeDesc;

	@NotNull
	private Integer connectionType;
	private String connectionTypeDesc;

	@NotBlank
	private String name;

	private Integer direction;
	private String directionDesc;

	private String ipAddress;
	private Integer port;

	private String comPort;
	private Integer baudRate;

	private String username;
	private String password;
	private String model;
	private String firmwareVersion;
	private String serialNumber;

	private Integer status = 1;
	private String statusDesc;

	private String remarks;

	private String accessUrl;

	private Integer lastHealthStatus;
	private String lastHealthStatusDesc;
	private Integer lastLatencyMs;
	private LocalDateTime lastCheckedAt;
	private String lastStatusNote;

	public GateDeviceDTO(GateDevice device) {
		super(device);
		if (CommonValidators.isValidObject(device.getGate())) {
			this.gateId = device.getGate().getId();
			this.gateName = device.getGate().getName();
		}
		this.deviceType = device.getDeviceType();
		this.deviceTypeDesc = GateDeviceType.getDescByCode(deviceType);
		this.connectionType = device.getConnectionType();
		this.connectionTypeDesc = DeviceConnectionType.getDescByCode(connectionType);
		this.name = device.getName();
		this.direction = device.getDirection();
		this.directionDesc = GateType.getDescByCode(direction);
		this.ipAddress = device.getIpAddress();
		this.port = device.getPort();
		this.comPort = device.getComPort();
		this.baudRate = device.getBaudRate();
		this.username = device.getUsername();
		this.password = device.getPassword();
		this.model = device.getModel();
		this.firmwareVersion = device.getFirmwareVersion();
		this.serialNumber = device.getSerialNumber();
		this.status = device.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
		this.remarks = device.getRemarks();
		this.accessUrl = device.getAccessUrl();
		this.lastHealthStatus = device.getLastHealthStatus();
		this.lastHealthStatusDesc = DeviceHealthStatus.getDescByCode(lastHealthStatus);
		this.lastLatencyMs = device.getLastLatencyMs();
		this.lastCheckedAt = device.getLastCheckedAt();
		this.lastStatusNote = device.getLastStatusNote();
	}
}	