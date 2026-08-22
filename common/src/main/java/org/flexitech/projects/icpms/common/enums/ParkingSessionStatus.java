package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum ParkingSessionStatus {
	ACTIVE(1, "Active"), COMPLETED(2, "Completed"), CANCELLED(3, "Cancelled");

	private final Integer code;
	private final String desc;

	ParkingSessionStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (ParkingSessionStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (ParkingSessionStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}
		return null;
	}
}
