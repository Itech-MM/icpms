package org.flexitech.projects.icpms.service.client.home;

import org.flexitech.projects.icpms.dto.api.response.home.HomePreloadResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface HomeScreenService {
	HomePreloadResponse getHomePreloadData(HttpServletRequest request) throws Exception;
}
