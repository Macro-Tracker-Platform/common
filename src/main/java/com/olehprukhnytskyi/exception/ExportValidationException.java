package com.olehprukhnytskyi.exception;

import com.olehprukhnytskyi.exception.error.ExportErrorCode;

public class ExportValidationException extends BaseException {
    public ExportValidationException(String message) {
        super(ExportErrorCode.EXPORT_VALIDATION_ERROR, message);
    }
}
