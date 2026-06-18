package com.olehprukhnytskyi.exception;

import com.olehprukhnytskyi.exception.error.ExportErrorCode;

public class ExportNoDataException extends BaseException {
    public ExportNoDataException(String message) {
        super(ExportErrorCode.EXPORT_NO_DATA, message);
    }
}
