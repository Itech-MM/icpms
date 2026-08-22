package org.flexitech.projects.icpms.dto.gate;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
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

	@NotBlank
	private String name;

	private Integer direction;
	private String directionDesc;

	@NotBlank
	private String ipAddress;
	private Integer port;
	private String username;
	private String password;
	private String model;

	private Integer status = 1;
	private String statusDesc;

	private String remarks;

	public GateDeviceDTO(GateDevice device) {
		super(device);
		if (CommonValidators.isValidObject(device.getGate())) {
			this.gateId = device.getGate().getId();
			this.gateName = device.getGate().getName();
		}
		this.deviceType = device.getDeviceType();
		this.deviceTypeDesc = GateDeviceType.getDescByCode(deviceType);
		this.name = device.getName();
		this.direction = device.getDirection();
		this.directionDesc = GateType.getDescByCode(direction);
		this.ipAddress = device.getIpAddress();
		this.port = device.getPort();
		this.username = device.getUsername();
		this.password = device.getPassword();
		this.model = device.getModel();
		this.status = device.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
		this.remarks = device.getRemarks();
	}
}