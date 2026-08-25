package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum ShiftStatus {
	OPEN(1, "Open"), CLOSED(2, "Closed");

	private final Integer code;
	private final String desc;

	ShiftStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (ShiftStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (ShiftStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
