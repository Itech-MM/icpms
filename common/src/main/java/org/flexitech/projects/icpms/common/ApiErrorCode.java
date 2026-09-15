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
	public static final String NO_ACTIVE_SUBSCRIPTION = "NO_ACTIVE_SUBSCRIPTION";

	public static final String UNAUTHORIZED = "UNAUTHORIZED";
	public static final String INSUFFICIENT_MEMBER_BALANCE = "INSUFFICIENT_MEMBER_BALANCE";
	public static final String AUTH_METHOD_DISABLED = "AUTH_METHOD_DISABLED";
	public static final String INVALID_AUTH_METHOD = "INVALID_AUTH_METHOD";
}