package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum SlotStatus {
	AVAILABLE(1, "Available"), OCCUPIED(2, "Occupied"), RESERVED(3, "Reserved"), MAINTENANCE(4, "Under Maintenance");

	private final Integer code;
	private final String desc;

	SlotStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (SlotStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (SlotStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}
		return null;
	}
}
