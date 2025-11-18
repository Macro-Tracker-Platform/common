package com.olehprukhnytskyi.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {
	INTERNAL_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	BAD_REQUEST("Bad request", HttpStatus.BAD_REQUEST.value()),
	INVALID_DATE("Invalid date", HttpStatus.BAD_REQUEST.value()),
	UPSTREAM_SERVICE_UNAVAILABLE("Upstream service unavailable", HttpStatus.SERVICE_UNAVAILABLE.value()),
	VALIDATION_ERROR("Validation failed", HttpStatus.BAD_REQUEST.value());

	private final String title;
	private final int status;

	@Override
	public String getCode() {
		return this.name();
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int getStatus() {
		return status;
	}
}
