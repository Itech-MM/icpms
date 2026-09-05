package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum VehicleAlertType {
	UNKNOWN_PLATE(1, "Unknown Plate Detected"),
	MEMBER_EXPIRED(2, "Member Expired During Session"),
	BLACKLIST_DETECTED(3, "Blacklist Vehicle Detected");

	private final Integer code;
	private final String desc;

	VehicleAlertType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (VehicleAlertType t : values()) {
			result.add(new CommonEnumObject(t.code, t.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (VehicleAlertType t : values()) {
			if (t.code.equals(code))
				return t.desc;
		}
		return null;
	}
}
