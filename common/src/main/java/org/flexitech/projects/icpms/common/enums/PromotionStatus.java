package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum PromotionStatus {
	ACTIVE(1, "Active"), INACTIVE(2, "Inactive"), PAUSED(3, "Paused");

	private final Integer code;
	private final String desc;

	PromotionStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (PromotionStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (PromotionStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
