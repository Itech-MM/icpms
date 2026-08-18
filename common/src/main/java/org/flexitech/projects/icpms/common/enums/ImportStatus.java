package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum ImportStatus {
	SUCCESS(1, "Success"), PARTIAL_SUCCESS(2, "Partial Success"), FAILED(2, "Failed");

	private final Integer code;
	private final String desc;

	ImportStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (ImportStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (ImportStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
