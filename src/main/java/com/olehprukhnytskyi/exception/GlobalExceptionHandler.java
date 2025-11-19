package com.olehprukhnytskyi.exception;

import com.olehprukhnytskyi.dto.ProblemDetails;
import com.olehprukhnytskyi.exception.error.BaseErrorCode;
import com.olehprukhnytskyi.exception.error.CommonErrorCode;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private String getTraceId() {
        Context currentContext = Context.current();
        Span activeSpan = Span.fromContext(currentContext);
        return activeSpan.getSpanContext().isValid()
                ? activeSpan.getSpanContext().getTraceId()
                : "N/A";
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            BindException.class
    })
    public ResponseEntity<ProblemDetails> handleValidationExceptions(Exception ex) {
        BaseErrorCode errorCode = CommonErrorCode.VALIDATION_ERROR;
        ProblemDetails.ProblemDetailsBuilder builder = ProblemDetails.builder()
                .title(errorCode.getTitle())
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .traceId(getTraceId());

        if (ex instanceof MissingServletRequestParameterException e) {
            builder.detail("Missing required query parameter");
            builder.invalidParams(List.of(
                    new ProblemDetails.InvalidParam(e.getParameterName(), "Parameter is required")
            ));
        } else if (ex instanceof MethodArgumentTypeMismatchException e) {
            builder.detail("Invalid parameter value");
            builder.invalidParams(List.of(
                    new ProblemDetails.InvalidParam(e.getName(), "Invalid type")
            ));
        } else if (ex instanceof MethodArgumentNotValidException e) {
            builder.detail("Validation failed for one or more parameters");
            builder.invalidParams(
                    e.getBindingResult().getFieldErrors().stream()
                            .map(err -> new ProblemDetails
                                    .InvalidParam(err.getField(), err.getDefaultMessage()))
                            .toList()
            );
        } else if (ex instanceof ConstraintViolationException e) {
            builder.detail("Validation failed for one or more parameters");
            builder.invalidParams(
                    e.getConstraintViolations().stream()
                            .map(err -> new ProblemDetails
                                    .InvalidParam(err.getPropertyPath().toString(), err.getMessage()))
                            .toList()
            );
        } else if (ex instanceof BindException e) {
            builder.detail("Validation failed for one or more parameters");
            builder.invalidParams(
                    e.getBindingResult().getFieldErrors().stream()
                            .map(err -> new ProblemDetails
                                    .InvalidParam(err.getField(), err.getDefaultMessage()))
                            .toList()
            );
        } else {
            builder.detail("Malformed request");
        }
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(builder.build());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ProblemDetails> handleBaseException(BaseException ex) {
        BaseErrorCode code = ex.getErrorCode();
        ProblemDetails body = ProblemDetails.builder()
                .title(code.getTitle())
                .status(code.getStatus())
                .code(code.getCode())
                .detail(ex.getMessage())
                .traceId(getTraceId())
                .build();
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred:", ex);
        BaseErrorCode errorCode = CommonErrorCode.INTERNAL_ERROR;
        ProblemDetails problemDetails = ProblemDetails.builder()
                .title(errorCode.getTitle())
                .status(errorCode.getStatus())
                .detail("An unexpected error occurred")
                .traceId(getTraceId())
                .code(errorCode.getCode())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problemDetails);
    }
}
