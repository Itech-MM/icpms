package org.flexitech.projects.icpms.persistence.entities.member;

import java.util.Date;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.slot.ParkingSlot;

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
@Table(name = TableNames.MEMBER_TBL)
@Getter
@Setter
public class Member extends BasedEntity {

	private String name;

	@Column(name = "phone_number")
	private String phoneNumber;

	private String email;

	/** MembershipType enum code: 1=Regular, 2=VIP, 3=Corporate */
	@Column(name = "membership_type")
	private Integer membershipType;

	@Column(name = "is_vip")
	private Boolean isVip = false;

	@Temporal(TemporalType.DATE)
	@Column(name = "valid_until")
	private Date validUntil;

	/** Reserved VIP slot (optional) */
	@ManyToOne
	@JoinColumn(name = "reserved_slot_id")
	private ParkingSlot reservedSlot;

	private Integer status = 1;
}
