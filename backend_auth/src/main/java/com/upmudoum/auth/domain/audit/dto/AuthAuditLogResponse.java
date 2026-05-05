package com.upmudoum.auth.domain.audit.dto;

import com.upmudoum.auth.domain.audit.vo.AuthAuditAction;
import com.upmudoum.auth.domain.audit.vo.AuthAuditOutcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class AuthAuditLogResponse {

    private final Long id;
    private final AuthAuditAction action;
    private final AuthAuditOutcome outcome;
    private final String subject;
    private final String method;
    private final String path;
    private final String clientIp;
    private final String resultCode;
    private final String detail;
    private final Instant createdAt;

}
