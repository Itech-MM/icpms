package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum PaymentMethod {
	CASH(1, "Cash"), CARD(2, "Card"), EWALLET(3, "E-Wallet"), ONLINE(4, "Online Banking");

	private final Integer code;
	private final String desc;

	PaymentMethod(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (PaymentMethod s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (PaymentMethod s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}
		return null;
	}
}
