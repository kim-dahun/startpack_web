package com.upmudoum.auth.domain.audit.controller;

import com.upmudoum.auth.common.api.ApiResponse;
import com.upmudoum.auth.domain.audit.dto.AuthAuditLogResponse;
import com.upmudoum.auth.domain.audit.service.AuthAuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/auth/audits", "/api/auth/audits"})
public class AuthAuditController {

    private final AuthAuditService authAuditService;

    public AuthAuditController(AuthAuditService authAuditService) {
        this.authAuditService = authAuditService;
    }

    @GetMapping
    public ApiResponse<List<AuthAuditLogResponse>> readLatest(@RequestParam(required = false) String subject) {
        return ApiResponse.success(authAuditService.readLatest(subject));
    }
}
