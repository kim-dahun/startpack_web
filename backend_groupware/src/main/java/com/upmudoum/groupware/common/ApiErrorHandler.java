package com.upmudoum.groupware.common;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(GroupwareException.class)
    public ResponseEntity<Map<String, Object>> handleGroupware(GroupwareException exception) {
        GroupwareErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(error(errorCode.getStatus(), exception.getMessage(), errorCode.getCode()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return ResponseEntity.status(status).body(error(status, exception.getReason(), "GROUPWARE_" + status.value() + "_000"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        GroupwareErrorCode errorCode = GroupwareErrorCode.REQUEST_VALIDATION_FAILED;
        return ResponseEntity.badRequest().body(error(errorCode.getStatus(), errorCode.getDefaultMessage(), errorCode.getCode()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST, exception.getMessage(), "GROUPWARE_400_000"));
    }

    private Map<String, Object> error(HttpStatus status, String message, String errorCode) {
        return Map.of(
                "timestamp", Instant.now(),
                "status", status.value(),
                "responseCode", status.value(),
                "errorCode", errorCode,
                "message", message == null ? status.getReasonPhrase() : message);
    }
}
