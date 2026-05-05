package com.upmudoum.auth.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.upmudoum.auth.domain.auth.dto.OpenApiTokenRequest;
import com.upmudoum.auth.domain.auth.dto.OpenApiTokenResponse;
import com.upmudoum.auth.domain.auth.entity.OpenApiClient;
import com.upmudoum.auth.domain.auth.repository.OpenApiClientRepository;
import com.upmudoum.auth.exception.ApiException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenApiClientAuthenticationIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private OpenApiClientRepository openApiClientRepository;

    @BeforeEach
    void setUp() {
        openApiClientRepository.deleteAll();
    }

    @Test
    void issueOpenApiTokenUsesOpenApiClientRepository() {
        openApiClientRepository.save(OpenApiClient.builder()
                .clientId("trade-client")
                .clientSecretHash(OpenApiClient.hashSecret("trade-secret"))
                .subject("system-trader")
                .scopesCsv("trade.read,trade.write")
                .enabled(true)
                .createdAt(Instant.now())
                .build());

        OpenApiTokenResponse response = authService.issueOpenApiToken(
                new OpenApiTokenRequest("trade-client", "trade-secret", "system-trader")
        );

        assertThat(response.getClientId()).isEqualTo("trade-client");
        assertThat(response.getSubject()).isEqualTo("system-trader");
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getScopes()).containsExactly("trade.read", "trade.write");
    }

    @Test
    void issueOpenApiTokenRejectsWrongSecret() {
        openApiClientRepository.save(OpenApiClient.builder()
                .clientId("trade-client")
                .clientSecretHash(OpenApiClient.hashSecret("trade-secret"))
                .subject("system-trader")
                .scopesCsv("trade.read")
                .enabled(true)
                .createdAt(Instant.now())
                .build());

        assertThatThrownBy(() -> authService.issueOpenApiToken(
                new OpenApiTokenRequest("trade-client", "wrong-secret", "system-trader")
        )).isInstanceOf(ApiException.class);
    }
}
