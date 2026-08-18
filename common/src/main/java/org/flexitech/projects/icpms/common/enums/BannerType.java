package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;

import org.flexitech.projects.icpms.common.CommonEnumObject;

import lombok.Getter;

@Getter
public enum BannerType {
	NORMAL(1, "Normal"), WEBSITE(2, "Website"), PRODUCT(3, "Product"), PROMOTION(4, "Promotion");

	private final Integer code;
	private final String desc;

	BannerType(int i, String string) {
		this.code = i;
		this.desc = string;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (BannerType s : values()) {
			result.add(new CommonEnumObject(s.code, s.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {

		for (BannerType s : values()) {
			if (s.code.equals(code))
				return s.desc;
		}

		return null;
	}
}
