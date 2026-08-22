package org.flexitech.projects.icpms.dto;

import java.util.List;

import org.flexitech.projects.icpms.common.CommonConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchResultDTO<T> {
	private Integer pageNo;
	private Integer limit = CommonConstants.ROW_PER_PAGE;
	private Integer totalPage;
	private Integer totalRecords;
	private Integer pageCount;
	private Boolean hasNextPage;
	private List<T> results;
}
