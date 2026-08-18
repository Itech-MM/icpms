package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum RewardType {
	DISCOUNT(1, "Discount"), CASHBACK(2, "Cashback"), FREE_GIFT(3, "Free gift"), TIRED(4, "Tired");

	private final Integer code;
	private final String desc;

	RewardType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (RewardType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (RewardType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
