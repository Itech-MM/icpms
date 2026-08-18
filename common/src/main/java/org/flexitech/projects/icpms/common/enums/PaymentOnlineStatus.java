package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

public enum PaymentOnlineStatus {
	ONLINE(1, "Online"), OFFLINE(2, "Offline");

	private final Integer code;
	private final String desc;

	PaymentOnlineStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (PaymentOnlineStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (PaymentOnlineStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
