package com.upmudoum.groupware.common;

import org.springframework.http.HttpStatus;

public enum GroupwareErrorCode {

    REQUEST_VALIDATION_FAILED("GROUPWARE_400_001", HttpStatus.BAD_REQUEST, "request validation failed"),
    REQUIRED_HEADER_MISSING("GROUPWARE_400_002", HttpStatus.BAD_REQUEST, "required header is missing"),
    GATEWAY_REQUIRED("GROUPWARE_403_001", HttpStatus.FORBIDDEN, "Groupware requests must pass through gateway"),
    USER_INACTIVE("GROUPWARE_403_101", HttpStatus.FORBIDDEN, "inactive user cannot receive messages"),
    PROJECT_NOT_VISIBLE("GROUPWARE_403_201", HttpStatus.FORBIDDEN, "project is not visible"),
    APPROVAL_DRAFTER_ONLY("GROUPWARE_403_301", HttpStatus.FORBIDDEN, "only drafter can submit approval document"),
    APPROVAL_CURRENT_APPROVER_ONLY("GROUPWARE_403_302", HttpStatus.FORBIDDEN, "only current approver can change approval status"),
    APPROVAL_CONSULT_RECEIVER_ONLY("GROUPWARE_403_303", HttpStatus.FORBIDDEN, "only current consult receiver can reset consult lines"),
    APPROVAL_DOCUMENT_NOT_FOUND("GROUPWARE_404_301", HttpStatus.NOT_FOUND, "approval document not found"),
    APPROVAL_LINE_TEMPLATE_NOT_FOUND("GROUPWARE_404_302", HttpStatus.NOT_FOUND, "approval line template not found"),
    APPROVAL_INVALID_STATE("GROUPWARE_409_301", HttpStatus.CONFLICT, "invalid approval state"),
    APPROVAL_TARGET_NOT_FOUND("GROUPWARE_409_302", HttpStatus.CONFLICT, "approval target user not found");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    GroupwareErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
