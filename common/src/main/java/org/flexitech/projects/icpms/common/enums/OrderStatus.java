package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum OrderStatus {
	 PENDING(2, "Pending"), CONFIRMED(3, "Confirmed"), DELIVERY(4, "Delivery"), SHIP(5, "Shipped"), FINISHED(1, "Finished"), CANCEL(6, "Cancel");
	// 1=finished,2=Pending,3=Delivery,4=Ship,5=Cancel
	private final Integer code;
	private final String desc;

	OrderStatus(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (OrderStatus s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (OrderStatus s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
