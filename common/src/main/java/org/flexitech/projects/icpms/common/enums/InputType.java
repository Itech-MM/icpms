package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum InputType {
	TEXT(1, "text"), NUMBER(2, "number"), TOGGLE(3, "toogle"), DATE(4, "date"), PASSWORD(5, "password");

	private final Integer code;
	private final String desc;

	InputType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (InputType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (InputType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
