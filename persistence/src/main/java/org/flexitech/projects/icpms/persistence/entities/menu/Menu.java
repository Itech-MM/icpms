package org.flexitech.projects.icpms.persistence.entities.menu;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = TableNames.MENU_TBL)
public class Menu extends BasedEntity{
	private String name;
	private String code;
	private String description;
	private String icon;
	private String url;
	private Integer status;
	private Integer sequence;

	@Column(name = "display_status")
	private Integer displayStatus = 1;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Menu parentMenu;
}