package org.flexitech.projects.icpms.persistence.entities.vehicle;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.VEHICLE_TBL)
@Getter
@Setter
public class Vehicle extends BasedEntity {

	@Column(name = "plate_number")
	private String plateNumber;

	@Column(name = "vehicle_type")
	private String vehicleType;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	private Integer status = 1;
}
