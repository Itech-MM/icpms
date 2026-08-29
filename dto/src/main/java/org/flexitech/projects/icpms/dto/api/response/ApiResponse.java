package org.flexitech.projects.icpms.dto.api.response;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private Instant timestamp = Instant.now();

	public ApiResponse(boolean success, String message, T data) {
		this.success = success;
		this.message = message;
		this.data = data;
		this.timestamp = Instant.now();
	}

	public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
		return ResponseEntity.ok(new ApiResponse<>(true, null, data));
	}

	public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
		return ResponseEntity.ok(new ApiResponse<>(true, message, data));
	}

	public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
	}

	public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message, T data) {
		return ResponseEntity.status(status).body(new ApiResponse<>(false, message, data));
	}

	public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
		return error(HttpStatus.BAD_REQUEST, message);
	}
	
	public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
		return error(HttpStatus.UNAUTHORIZED, message);
	}

	public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
		return error(HttpStatus.NOT_FOUND, message);
	}

	public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
		return error(HttpStatus.CONFLICT, message);
	}

	public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}
}