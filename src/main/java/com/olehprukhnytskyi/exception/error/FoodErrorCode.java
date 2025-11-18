package com.olehprukhnytskyi.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum FoodErrorCode implements BaseErrorCode {
	FOOD_NOT_FOUND("Food not found", HttpStatus.NOT_FOUND.value()),
	FOOD_ALREADY_EXISTS("Food already exists", HttpStatus.CONFLICT.value());

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
