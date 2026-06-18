package com.olehprukhnytskyi.exception.error;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ExportErrorCode implements BaseErrorCode {
    EXPORT_VALIDATION_ERROR("Export validation failed", HttpStatus.BAD_REQUEST.value()),
    EXPORT_NO_DATA("Export data not found", HttpStatus.NOT_FOUND.value());

    private final String title;
    private final int status;

    @Override
    public String getCode() {
        return name();
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
