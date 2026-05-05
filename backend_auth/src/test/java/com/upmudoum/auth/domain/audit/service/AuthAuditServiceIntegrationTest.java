package com.upmudoum.auth.domain.audit.service;

import com.upmudoum.auth.domain.audit.vo.AuthAuditAction;
import com.upmudoum.auth.domain.audit.vo.AuthAuditOutcome;
import com.upmudoum.auth.domain.auth.dto.LoginGroupRequest;
import com.upmudoum.auth.domain.auth.dto.LoginRequest;
import com.upmudoum.auth.domain.auth.dto.OpenApiTokenRequest;
import com.upmudoum.auth.domain.auth.service.AuthService;
import com.upmudoum.auth.domain.token.dto.TokenVerifyRequest;
import com.upmudoum.auth.domain.token.service.TokenService;
import com.upmudoum.auth.domain.token.vo.TokenType;
import com.upmudoum.auth.exception.ApiException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AuthAuditServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthAuditService authAuditService;

    @Test
    void recordsSuccessAndFailureAuditEvents() {
        authService.login(loginRequest(List.of("ERP")));

        assertThatThrownBy(() -> authService.login(loginRequest(List.of("TRADE"))))
                .isInstanceOf(ApiException.class);

        assertThat(authAuditService.readLatest("user-100"))
                .anyMatch(log -> log.getAction() == AuthAuditAction.LOGIN && log.getOutcome() == AuthAuditOutcome.SUCCESS);
        assertThat(authAuditService.readLatest("demo-user"))
                .anyMatch(log -> log.getAction() == AuthAuditAction.LOGIN && log.getOutcome() == AuthAuditOutcome.FAILURE);
    }

    @Test
    void recordsOpenApiFailureAndTokenVerifyFailure() {
        assertThatThrownBy(() -> authService.issueOpenApiToken(
                new OpenApiTokenRequest("trade-client", "bad-secret", "system-trader")))
                .isInstanceOf(ApiException.class);

        assertThatThrownBy(() -> tokenService.verify(new TokenVerifyRequest("broken-token", TokenType.ACCESS)))
                .isInstanceOf(ApiException.class);

        assertThat(authAuditService.readLatest("system-trader"))
                .anyMatch(log -> log.getAction() == AuthAuditAction.OPEN_API_TOKEN_ISSUE && log.getOutcome() == AuthAuditOutcome.FAILURE);
        assertThat(authAuditService.readLatest(null))
                .anyMatch(log -> log.getAction() == AuthAuditAction.TOKEN_VERIFY && log.getOutcome() == AuthAuditOutcome.FAILURE);
    }

    @Test
    void recordsFailureWhenRequestedServiceIsNotAccessible() {
        assertThatThrownBy(() -> authService.login(loginRequest(List.of("TRADE"))))
                .isInstanceOf(ApiException.class)
                .hasMessage("Service access denied.");

        assertThat(authAuditService.readLatest("demo-user"))
                .anyMatch(log -> log.getAction() == AuthAuditAction.LOGIN
                        && log.getOutcome() == AuthAuditOutcome.FAILURE
                        && "AUTH_010".equals(log.getResultCode()));
    }

    private LoginRequest loginRequest(List<String> serviceAccesses) {
        return new LoginRequest(
                "COM001",
                "user-100",
                "demo-user",
                "ERP",
                serviceAccesses,
                List.of(new LoginGroupRequest("COM001", "ERP", "ADMIN", "ADMIN")),
                List.of("USER")
        );
    }
}
