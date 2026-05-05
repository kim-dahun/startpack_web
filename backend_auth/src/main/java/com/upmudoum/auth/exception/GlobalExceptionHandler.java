package com.upmudoum.auth.exception;

import com.upmudoum.auth.common.api.ApiResponse;
import com.upmudoum.auth.common.api.ErrorResponseData;
import com.upmudoum.auth.common.web.RequestContextProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final RequestContextProvider requestContextProvider;

    public GlobalExceptionHandler(RequestContextProvider requestContextProvider) {
        this.requestContextProvider = requestContextProvider;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<ErrorResponseData>> handleApiException(ApiException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.status())
                .body(ApiResponse.failure(errorCode.code(), errorCode.message(), errorData()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponseData>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(ApiResponse.failure(ErrorCode.INVALID_REQUEST.code(), message, errorData()));
    }

    private ErrorResponseData errorData() {
        return new ErrorResponseData(java.time.Instant.now(), requestContextProvider.currentPath());
    }
}
