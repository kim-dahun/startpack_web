package com.upmudoum.auth.domain.audit.vo;

public enum AuthAuditAction {
    LOGIN,
    OPEN_API_TOKEN_ISSUE,
    TOKEN_VERIFY,
    TOKEN_REFRESH,
    TOKEN_LOGOUT,
    TOKEN_FORCE_EXPIRE
}
