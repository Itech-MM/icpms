package org.flexitech.projects.icpms.service.client.home;

import java.util.List;

import org.flexitech.projects.icpms.common.CommonConstants;
import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.dto.api.response.home.HomePreloadResponse;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDTO;
import org.flexitech.projects.icpms.dto.site.SiteDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingDTO;
import org.flexitech.projects.icpms.service.gate.GateDeviceService;
import org.flexitech.projects.icpms.service.gate.GateService;
import org.flexitech.projects.icpms.service.setting.SystemSettingService;
import org.flexitech.projects.icpms.service.site.SiteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HomeScreenServiceImpl implements HomeScreenService {
	
	private final GateService gateService;
	
	private final GateDeviceService gateDeviceService;
	
	private final SiteService siteService;
	
	private final SystemSettingService systemSettingService;
	
	@Override
	public HomePreloadResponse getHomePreloadData(HttpServletRequest request) throws Exception {
		HomePreloadResponse response = new HomePreloadResponse();
		
		String gateIpHeader = request.getHeader(CommonConstants.GATE_IP_HEADER);
		
		if(!CommonValidators.validString(gateIpHeader)) {
			throw new RuntimeException("Invalid gate header!");
		}
		
		GateDTO gate = gateService.findByIpAddress(gateIpHeader);
		
		response.setGate(gate);
		
		List<GateDeviceDTO> devices = gateDeviceService.getDevicesByGate(gate.getId());
		
		response.setDevices(devices);
		
		List<SystemSettingDTO> settings = this.systemSettingService.getAllOperatorSettings();
		
		if(CommonValidators.validList(settings)) {
			response.setSettings(settings);
		}
		
		if(CommonValidators.validLong(gate.getSiteId())) {
			SiteDTO site = siteService.getSiteById(gate.getSiteId());
			response.setSite(site);
		}
		
		return response;
	}

}
