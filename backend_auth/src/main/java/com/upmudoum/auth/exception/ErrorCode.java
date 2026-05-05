package com.upmudoum.auth.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "Invalid credentials."),
    INVALID_OPEN_API_CLIENT(HttpStatus.UNAUTHORIZED, "AUTH_002", "Invalid open api client."),
    TOKEN_ISSUE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_003", "Token issue failed."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "Invalid token."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_005", "Token expired."),
    TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "AUTH_006", "Token revoked."),
    TOKEN_TYPE_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH_007", "Token type mismatch."),
    REFRESH_TOKEN_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, "AUTH_008", "Refresh token reuse detected."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_009", "Refresh token not found."),
    SERVICE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_010", "Service access denied."),
    GATEWAY_ACCESS_REQUIRED(HttpStatus.FORBIDDEN, "AUTH_011", "Gateway access required."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_012", "Refresh token required."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "Invalid request.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
