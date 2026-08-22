package org.flexitech.projects.icpms.dto.vehicle;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.vehicle.Vehicle;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO extends CommonDTO {

	@NotBlank
	private String plateNumber;
	private String vehicleType;
	private Long memberId;
	private String memberName;
	private Integer status = 1;
	private String statusDesc;

	public VehicleDTO(Vehicle vehicle) {
		super(vehicle);
		this.plateNumber = vehicle.getPlateNumber();
		this.vehicleType = vehicle.getVehicleType();
		if (CommonValidators.isValidObject(vehicle.getMember())) {
			this.memberId = vehicle.getMember().getId();
			this.memberName = vehicle.getMember().getName();
		}
		this.status = vehicle.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
	}
}
