package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum DeviceHealthStatus {
	UNKNOWN(0, "Not checked"),
	ONLINE(1, "Online"),
	WARNING(2, "Degraded"),
	OFFLINE(3, "Offline");

	private final Integer code;
	private final String desc;

	DeviceHealthStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (DeviceHealthStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (DeviceHealthStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
