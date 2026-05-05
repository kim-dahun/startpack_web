package com.upmudoum.auth.domain.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.auth.domain.audit.entity.AuthAuditLog;
import com.upmudoum.auth.domain.audit.repository.AuthAuditLogRepository;
import com.upmudoum.auth.domain.audit.vo.AuthAuditAction;
import com.upmudoum.auth.domain.audit.vo.AuthAuditOutcome;
import com.upmudoum.auth.domain.auth.entity.OpenApiClient;
import com.upmudoum.auth.domain.token.entity.RefreshToken;
import com.upmudoum.auth.domain.token.repository.RefreshTokenRepository;
import com.upmudoum.auth.domain.token.vo.TokenType;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class AuthSchemaRepositoryIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OpenApiClientRepository openApiClientRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AuthAuditLogRepository authAuditLogRepository;

    @BeforeEach
    void setUp() {
        authAuditLogRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        openApiClientRepository.deleteAll();
    }

    @Test
    void repositoriesOperateOnlyOnAuthServiceSchema() {
        openApiClientRepository.save(OpenApiClient.builder()
                .clientId("trade-client")
                .clientSecretHash(OpenApiClient.hashSecret("trade-secret"))
                .subject("system-trader")
                .scopesCsv("trade.read")
                .enabled(true)
                .createdAt(Instant.now())
                .build());

        refreshTokenRepository.save(RefreshToken.builder()
                .tokenId("refresh-token-1")
                .subject("user-100")
                .tokenType(TokenType.REFRESH)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build());

        authAuditLogRepository.save(AuthAuditLog.builder()
                .action(AuthAuditAction.LOGIN)
                .outcome(AuthAuditOutcome.SUCCESS)
                .subject("user-100")
                .method("POST")
                .path("/api/auth/login")
                .clientIp("127.0.0.1")
                .resultCode("OK")
                .detail("schema check")
                .createdAt(Instant.now())
                .build());

        assertThat(tableExists("auth_service", "open_api_clients")).isEqualTo(1);
        assertThat(tableExists("auth_service", "refresh_tokens")).isEqualTo(1);
        assertThat(tableExists("auth_service", "auth_audit_logs")).isEqualTo(1);

        assertThat(jdbcTemplate.queryForObject("select count(*) from auth_service.open_api_clients", Integer.class))
                .isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("select count(*) from auth_service.refresh_tokens", Integer.class))
                .isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("select count(*) from auth_service.auth_audit_logs", Integer.class))
                .isEqualTo(1);
    }

    private Integer tableExists(String schemaName, String tableName) {
        return jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_schema = ? and table_name = ?",
                Integer.class,
                schemaName,
                tableName
        );
    }
}
