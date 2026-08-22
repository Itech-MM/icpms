package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum GateDeviceType {
	ANPR_CAMERA(1, "ANPR Camera (Barrier Control)"),
	LED_DISPLAY(2, "LED Display"),
	GATE_CONTROLLER(3, "Gate Controller / IO Box"),
	LOOP_DETECTOR(4, "Loop Detector"),
	INTERCOM(5, "Intercom"),
	OTHER(6, "Other");

	private final Integer code;
	private final String desc;

	GateDeviceType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (GateDeviceType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (GateDeviceType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}
		return null;
	}
}