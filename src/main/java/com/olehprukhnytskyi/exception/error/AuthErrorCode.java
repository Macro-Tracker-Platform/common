package com.olehprukhnytskyi.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
	INVALID_CREDENTIALS("Invalid credentials", HttpStatus.UNAUTHORIZED.value()),
	EMAIL_ALREADY_EXISTS("Email already exists", HttpStatus.CONFLICT.value()),
	UNSUPPORTED_PROVIDER("Unsupported social provider", HttpStatus.BAD_REQUEST.value()),
	PROVIDER_API_ERROR("Error from social provider API", HttpStatus.BAD_GATEWAY.value()),
	TOKEN_VERIFICATION_FAILED("Token verification failed", HttpStatus.UNAUTHORIZED.value()),
	INVALID_TOKEN("Invalid token", HttpStatus.UNAUTHORIZED.value()),
	EMAIL_NOT_CONFIRMED("Email is not confirmed", HttpStatus.FORBIDDEN.value());

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
