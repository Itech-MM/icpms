package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum BalanceTransactionType {
	CREDIT(1, "Credit"), DEBIT(2, "Debit"), REFUND(3, "Refund"), ADJUSTMENT(4, "Adjustment");

	private final Integer code;
	private final String desc;

	BalanceTransactionType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (BalanceTransactionType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (BalanceTransactionType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
