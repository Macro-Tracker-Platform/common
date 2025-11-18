package com.olehprukhnytskyi.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum EventErrorCode implements BaseErrorCode {
	KAFKA_SEND_FAILED("Failed to send Kafka event", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	KAFKA_PROCESSING_ERROR("Error processing Kafka event", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	EVENT_DESERIALIZATION_FAILED("Failed to deserialize event", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	EVENT_SERIALIZATION_FAILED("Failed to serialize event", HttpStatus.INTERNAL_SERVER_ERROR.value());

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
