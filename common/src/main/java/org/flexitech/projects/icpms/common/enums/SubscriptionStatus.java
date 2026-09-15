package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
	ACTIVE(1, "Active"), EXPIRED(2, "Inactive"), CANCELLED(3, "Cancel");

	private final Integer code;
	private final String desc;

	SubscriptionStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (SubscriptionStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (SubscriptionStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
