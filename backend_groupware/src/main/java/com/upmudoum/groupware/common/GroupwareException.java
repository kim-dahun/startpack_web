package com.upmudoum.groupware.common;

public class GroupwareException extends RuntimeException {

    private final GroupwareErrorCode errorCode;

    public GroupwareException(GroupwareErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public GroupwareException(GroupwareErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GroupwareErrorCode getErrorCode() {
        return errorCode;
    }
}
