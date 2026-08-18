package org.flexitech.projects.icpms.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
	private static final ObjectMapper parser = new ObjectMapper();
	static {
		parser.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static <T> T parse(String data, TypeReference<T> ref) {
		try {
			return parser.readValue(data, ref);
		} catch (JsonMappingException e) {
			log.error("Json Mapping Exception :: {}", ExceptionUtils.getStackTrace(e));
		} catch (JsonProcessingException e) {
			log.error("Json Processing Exception :: {}", ExceptionUtils.getStackTrace(e));
		}
		return null;
	}
	
	public static String toJson(Object data) {
		try {
			return parser.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Json Processing Exception :: {}", ExceptionUtils.getStackTrace(e));
		}
		return "";
	}
	
	public static String eToJson(Throwable e, Integer code, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("success", false);
        error.put("message", message);
        error.put("data", e.getMessage());
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(error);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
            json = e1.getMessage();
        }
        return json;
    }
}
