package org.flexitech.projects.icpms.dto.api.response.home;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;
import org.flexitech.projects.icpms.common.enums.GateDeviceType;
import org.flexitech.projects.icpms.common.enums.GateType;
import org.flexitech.projects.icpms.common.enums.PaymentMethod;
import org.flexitech.projects.icpms.dto.gate.GateDTO;
import org.flexitech.projects.icpms.dto.gate.GateDeviceDTO;
import org.flexitech.projects.icpms.dto.site.SiteDTO;
import org.flexitech.projects.icpms.dto.system_setting.SystemSettingDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HomePreloadResponse {
	private SiteDTO site;
	private GateDTO gate;
	private List<GateDeviceDTO> devices;
	
	private List<SystemSettingDTO> settings = new ArrayList<SystemSettingDTO>();
	
	// dropdown data
	private List<CommonEnumObject> deviceTypes = GateDeviceType.getAll();
	private List<CommonEnumObject> gateTypes = GateType.getAll();
	private List<CommonEnumObject> paymentMethods = PaymentMethod.getAll();
}
