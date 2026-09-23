package org.flexitech.projects.icpms.dto.menu;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.dto.CommonDTO;
import org.flexitech.projects.icpms.persistence.entities.menu.Menu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MenuDTO extends CommonDTO{
	private String name;
	private String code;
	private String description;
	private String icon;
	private String url;
	private Integer status;
	private String statusDesc;
	private Integer sequence;
	
	private Integer displayStatus;
	
	private String parentMenuName;
	private Long parentMenuId;
	
	private List<MenuDTO> children = new ArrayList<MenuDTO>();
	
	public MenuDTO(Menu m) {
		super(m);
		this.name = m.getName();
		this.code = m.getCode();
		this.description = m.getDescription();
		this.url = m.getUrl();
		this.icon = m.getIcon();
		this.status = m.getStatus();
		this.statusDesc = ActiveStatus.getDescByCode(status);
		this.sequence = m.getSequence();
		this.displayStatus = m.getDisplayStatus();
		
		if(CommonValidators.isValidObject(m.getParentMenu())) {
			this.parentMenuId = m.getParentMenu().getId();
			this.parentMenuName = m.getParentMenu().getName();
		}
	}
	
}
