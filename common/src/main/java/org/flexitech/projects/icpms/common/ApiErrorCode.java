package org.flexitech.projects.icpms.common;

public final class ApiErrorCode {

	private ApiErrorCode() {
	}

	public static final String UNKNOWN_PLATE = "UNKNOWN_PLATE";
	public static final String VEHICLE_NOT_FOUND = "VEHICLE_NOT_FOUND";
	public static final String VEHICLE_BLACKLISTED = "VEHICLE_BLACKLISTED";
	public static final String MEMBER_EXPIRED = "MEMBER_EXPIRED";
	public static final String NO_ACTIVE_SHIFT = "NO_ACTIVE_SHIFT";
	public static final String NO_ACTIVE_SESSION = "NO_ACTIVE_SESSION";
}