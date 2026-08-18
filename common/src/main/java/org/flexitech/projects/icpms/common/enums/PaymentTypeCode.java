package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum PaymentTypeCode {
	CASH(1, "Cash"), WALLET(2, "Wallet"), BANK_TRANSFER(3, "Bank Transfer");

	private final Integer code;
	private final String desc;

	PaymentTypeCode(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (PaymentTypeCode s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (PaymentTypeCode s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
