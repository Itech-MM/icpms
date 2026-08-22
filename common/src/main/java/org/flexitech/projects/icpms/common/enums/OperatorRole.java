package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum OperatorRole {
	GATE_OPERATOR(1, "Gate Operator"), SUPERVISOR(2, "Supervisor"), CASHIER(3, "Cashier");

	private final Integer code;
	private final String desc;

	OperatorRole(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (OperatorRole s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (OperatorRole s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}
		return null;
	}
}
