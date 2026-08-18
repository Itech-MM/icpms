package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum DiscountType {
	FIXED_AMOUNT(1, "Amount"), PERCENTAGE(2, "Percentage");

	private final Integer code;
	private final String desc;

	DiscountType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (DiscountType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (DiscountType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
