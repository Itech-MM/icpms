package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum CouponType {
	NORMAL(1, "Normal"), REFERRAL(2, "Referral"), WELCOME(3, "Welcome");

	private final Integer code;
	private final String desc;

	CouponType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (CouponType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (CouponType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
	
	public static CouponType getByCode(Integer code) {
		for (CouponType s : values()) {
			if (s.code.equals(code))
				return s;
		}

		return null;
	}
}
