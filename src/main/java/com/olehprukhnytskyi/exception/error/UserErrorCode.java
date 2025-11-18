package com.olehprukhnytskyi.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
	USER_PROFILE_NOT_FOUND("User profile not found", HttpStatus.NOT_FOUND.value()),
	USER_ALREADY_EXISTS("User already exists", HttpStatus.CONFLICT.value());

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
