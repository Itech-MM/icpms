package org.flexitech.projects.icpms.common.enums;

import java.util.ArrayList;
import java.util.List;
import org.flexitech.projects.icpms.common.CommonEnumObject;
import lombok.Getter;

@Getter
public enum OperatorAuthMethod {
	PASSWORD(1, "Username/Password", false, "AUTH_METHOD_PASSWORD"),
	RFID(2, "RFID Card", true, "AUTH_METHOD_RFID"),
	QR_CODE(3, "QR Code Scan", true, "AUTH_METHOD_QR"),
	MAG_STRIPE(4, "Magnetic Stripe Swipe", true, "AUTH_METHOD_SWIPE"),
	PIN(5, "Quick PIN", false, "AUTH_METHOD_PIN");

	private final Integer code;
	private final String desc;
	private final boolean requiresDevice;
	private final String settingCode; // maps to system_settings.code for this method's toggle row

	OperatorAuthMethod(Integer code, String desc, boolean requiresDevice, String settingCode) {
		this.code = code;
		this.desc = desc;
		this.requiresDevice = requiresDevice;
		this.settingCode = settingCode;
	}

	public static List<CommonEnumObject> getAll() {
		List<CommonEnumObject> result = new ArrayList<CommonEnumObject>();
		for (OperatorAuthMethod m : values()) {
			result.add(new CommonEnumObject(m.code, m.desc));
		}
		return result;
	}

	public static String getDescByCode(Integer code) {
		for (OperatorAuthMethod m : values()) {
			if (m.code.equals(code))
				return m.desc;
		}
		return null;
	}

	public static OperatorAuthMethod getByCode(Integer code) {
		for (OperatorAuthMethod m : values()) {
			if (m.code.equals(code))
				return m;
		}
		return null;
	}
}