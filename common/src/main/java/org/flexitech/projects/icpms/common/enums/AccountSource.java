package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum AccountSource {
	NORMAL(1, "Normal"), GOOGLE(2, "Google"), FACEBOOK(3, "Facebook"), TIKTOK(4, "Tiktok");

	private final Integer code;
	private final String desc;

	AccountSource(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (AccountSource s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (AccountSource s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}