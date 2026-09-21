package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum DeviceConnectionType {
	LAN(1, "Lan"), SERIAL(2, "Serial");

	private final Integer code;
	private final String desc;

	DeviceConnectionType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (DeviceConnectionType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (DeviceConnectionType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
