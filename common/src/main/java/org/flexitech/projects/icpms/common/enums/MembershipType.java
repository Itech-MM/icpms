package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum MembershipType {
	REGULAR(1, "Regular"), VIP(2, "VIP"), CORPORATE(3, "Corporate");

	private final Integer code;
	private final String desc;

	MembershipType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (MembershipType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (MembershipType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}
		return null;
	}
}
