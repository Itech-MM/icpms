package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum PaymentStatus {
	PAID(1, "Paid"), PENDING(2, "Pending"),UNPAID(3, "Unpaid");
	// 1=paid,2=unpaid
	private final Integer code;
	private final String desc;

	PaymentStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (PaymentStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (PaymentStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
