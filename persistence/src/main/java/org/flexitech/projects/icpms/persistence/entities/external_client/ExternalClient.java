package org.flexitech.projects.icpms.persistence.entities.external_client;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.EXTERNAL_CLIENT_TBL)
@Getter
@Setter
public class ExternalClient extends BasedEntity {

	private String name;
	
	private String description;

	@Column(name = "token_hash", nullable = false)
	private String tokenHash;

	private Integer status = 1;
}