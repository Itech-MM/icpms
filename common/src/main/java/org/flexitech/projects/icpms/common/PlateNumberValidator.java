package org.flexitech.projects.icpms.common;

import java.util.Set;
import java.util.regex.Pattern;

public final class PlateNumberValidator {

	private static final Set<String> UNKNOWN_LITERALS = Set.of(
			"UNKNOWN", "UNRECOGNIZED", "UNREAD", "N/A", "NA", "NOPLATE", "NO_PLATE", "-", "NULL");

	private static final Pattern VALID_PLATE_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]{4,10}$");

	private PlateNumberValidator() {
	}

	public static boolean isUnknownOrInvalid(String plateNumber) {
		if (plateNumber == null || plateNumber.isBlank()) {
			return true;
		}

		String normalized = plateNumber.trim().toUpperCase();

		if (UNKNOWN_LITERALS.contains(normalized)) {
			return true;
		}

		if (normalized.contains("/") || normalized.contains("-") || normalized.contains(" ")) {
			return true;
		}

		return !VALID_PLATE_PATTERN.matcher(normalized).matches();
	}
}