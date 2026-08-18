package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum ActiveStatus {
	ACTIVE(1, "Active"), INACTIVE(2, "Inactive");

	private final Integer code;
	private final String desc;

	ActiveStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (ActiveStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (ActiveStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
