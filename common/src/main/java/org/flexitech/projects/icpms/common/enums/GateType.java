package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum GateType {
	ENTRY(1, "Entry"), EXIT(2, "Exit"), BOTH(3, "Entry / Exit");

	private final Integer code;
	private final String desc;

	GateType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (GateType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (GateType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}
		return null;
	}
}
