package com.olehprukhnytskyi.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum IntakeErrorCode implements BaseErrorCode {
	INTAKE_NOT_FOUND("Intake not found", HttpStatus.NOT_FOUND.value());

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
