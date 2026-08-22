package org.flexitech.projects.icpms.dto;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.utils.DateUtils;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class CommonDTO {
	private Long id;
	private String createdTime;
	private String updatedTime;
	private Long createdBy;
	private String createdByName;
	private Long updatedBy;
	private String updatedByName;
	
	public CommonDTO(BasedEntity entity) {
		this.id = entity.getId();
		if(CommonValidators.isValidObject(entity.getCreatedTime())) {
			this.createdTime = DateUtils.dateToString(entity.getCreatedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}
		if(CommonValidators.isValidObject(entity.getUpdatedTime())) {
			this.updatedTime = DateUtils.dateToString(entity.getUpdatedTime(), CommonConstants.STANDARD_12_HOUR_DATE_MINUTE_FORMAT);
		}
		if(CommonValidators.isValidObject(entity.getCreatedBy())) {
			this.createdBy = entity.getCreatedBy().getId();
			this.createdByName = entity.getCreatedBy().getName();
		}
		if(CommonValidators.isValidObject(entity.getUpdatedBy())) {
			this.updatedBy = entity.getUpdatedBy().getId();
			this.updatedByName = entity.getUpdatedBy().getName();
		}
	}
	
}
