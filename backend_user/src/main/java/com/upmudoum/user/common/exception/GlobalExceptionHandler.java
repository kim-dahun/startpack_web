package com.upmudoum.user.common.exception;

import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.common.response.FieldErrorDetail;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.fail(exception.getMessage(), errorCode.getStatus().value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        List<FieldErrorDetail> fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorDetail)
                .toList();
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        String message = fields.stream()
                .map(field -> field.getField() + ": " + field.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.fail(message, errorCode.getStatus().value()));
    }

    private FieldErrorDetail toFieldErrorDetail(FieldError fieldError) {
        return new FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
