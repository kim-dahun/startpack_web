package com.upmudoum.auth.domain.audit.service;

import com.upmudoum.auth.common.web.RequestContextProvider;
import com.upmudoum.auth.domain.audit.dto.AuthAuditLogResponse;
import com.upmudoum.auth.domain.audit.entity.AuthAuditLog;
import com.upmudoum.auth.domain.audit.repository.AuthAuditLogRepository;
import com.upmudoum.auth.domain.audit.vo.AuthAuditAction;
import com.upmudoum.auth.domain.audit.vo.AuthAuditOutcome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthAuditService {

    private final AuthAuditLogRepository authAuditLogRepository;
    private final RequestContextProvider requestContextProvider;

    public AuthAuditService(AuthAuditLogRepository authAuditLogRepository, RequestContextProvider requestContextProvider) {
        this.authAuditLogRepository = authAuditLogRepository;
        this.requestContextProvider = requestContextProvider;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(AuthAuditAction action, AuthAuditOutcome outcome, String subject, String resultCode, String detail) {
        authAuditLogRepository.save(AuthAuditLog.builder()
                .action(action)
                .outcome(outcome)
                .subject(subject)
                .method(requestContextProvider.currentMethod())
                .path(requestContextProvider.currentPath())
                .clientIp(requestContextProvider.currentClientIp())
                .resultCode(resultCode)
                .detail(detail)
                .createdAt(Instant.now())
                .build());
    }

    @Transactional(readOnly = true)
    public List<AuthAuditLogResponse> readLatest(String subject) {
        List<AuthAuditLog> logs = (subject == null || subject.isBlank())
                ? authAuditLogRepository.findTop50ByOrderByCreatedAtDesc()
                : authAuditLogRepository.findTop50BySubjectOrderByCreatedAtDesc(subject);

        return logs.stream()
                .map(log -> AuthAuditLogResponse.builder()
                        .id(log.getId())
                        .action(log.getAction())
                        .outcome(log.getOutcome())
                        .subject(log.getSubject())
                        .method(log.getMethod())
                        .path(log.getPath())
                        .clientIp(log.getClientIp())
                        .resultCode(log.getResultCode())
                        .detail(log.getDetail())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
