package org.flexitech.projects.icpms.persistence.entities.gate;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.site.Site;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.GATE_TBL)
@Getter
@Setter
public class Gate extends BasedEntity {

	@ManyToOne
	@JoinColumn(name = "site_id")
	private Site site;

	private String name;
	private String code;

	/** GateType enum code: 1=Entry, 2=Exit, 3=Both */
	private Integer type;

	private Integer status = 1;
	
	@Column(name ="gate_ip_address")
	private String gateIpAddress;
}
