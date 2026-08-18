package org.flexitech.projects.icpms.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.google.myanmartools.TransliterateU2Z;
import com.google.myanmartools.TransliterateZ2U;
import com.google.myanmartools.ZawgyiDetector;

public class CommonUtils {
	private static final DecimalFormat formatter = new DecimalFormat("#,###");

	public static String formatNumber(Number b) {
		return (b != null) ? formatter.format(b) : "0";
	}

	public static Number formatNumber(String s) {
		try {
			return (s != null && !s.isEmpty()) ? formatter.parse(s) : 0;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static Integer convertNumberToInteger(Number value) {
		if (value == null)
			return 0;
		return value.intValue();
	}

	public static String UnicodeToZawgyi(String value) {
		if (value != null) {

			if (isUnicode(value)) {
				final TransliterateU2Z u2Z = new TransliterateU2Z("Unicode to Zawgyi");
				String result = u2Z.convert(value);
				return result;
			} else {
				return value;
			}

		}
		return "";
	}

	public static String ZawgyiToUnicode(String string) {
		if (string != null) {
			final TransliterateZ2U z2U = new TransliterateZ2U("Zawgyi to Unicode");
			String result = z2U.convert(string);
			return result;
		}
		return "";
	}

	public static boolean isUnicode(String string) {
		final ZawgyiDetector detector = new ZawgyiDetector();
		DecimalFormat df2 = new DecimalFormat("#.#");
		double score = detector.getZawgyiProbability(string);
		df2.setRoundingMode(RoundingMode.UP);
		String uniorzawgyi = String.valueOf(df2.format(score));
		if (uniorzawgyi.equals("1")) {
			return false;
		} else {
			return true;
		}
	}

	public static String analyseReportContent(String string) {
		if (string == null)
			return "";
		if (isUnicode(string)) {
			return UnicodeToZawgyi(string);
		}
		return string;
	}

	public static <T> T getDefaultValue(T v, T defaultValue) {
		return CommonValidators.isValidObject(v) ? v : defaultValue;
	}
	
	public static Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        for (ObjectError error : bindingResult.getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }

        return errors;
    }
	
	public static Map<String, String> getErrors(BindingResult bindingResult, MessageSource messageSource) {
	    Map<String, String> errors = new HashMap<>();

	    for (FieldError error : bindingResult.getFieldErrors()) {
	        String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
	        errors.put(error.getField(), message);
	    }

	    for (ObjectError error : bindingResult.getGlobalErrors()) {
	        String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
	        errors.put(error.getObjectName(), message);
	    }

	    return errors;
	}
	
	public static ResponseEntity<Map<String, Object>> buildSuccessResponse(String message, Object data) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", message);
		response.put("data", data);
		return ResponseEntity.ok(response);
	}

	public static ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", message);
		return ResponseEntity.status(status).body(response);
	}
	
	public static ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message,
			Object error) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", message);
		if (error != null)
			response.put("error", error);
		return ResponseEntity.status(status).body(response);
	}
	
	public static BigDecimal roundToNearest50(BigDecimal amount) {
	    BigDecimal fifty = new BigDecimal("50");
	    BigDecimal divided = amount.divide(fifty, 0, RoundingMode.HALF_UP);
	    return divided.multiply(fifty);
	}
	
	
	public static String generateRandomCode() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();
	}
	
}
