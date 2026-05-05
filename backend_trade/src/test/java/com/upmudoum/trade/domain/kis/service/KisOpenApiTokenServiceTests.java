package com.upmudoum.trade.domain.kis.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.kis.entity.KisOpenApiToken;
import com.upmudoum.trade.domain.kis.infra.KisOpenApiTokenClient;
import com.upmudoum.trade.domain.kis.infra.KisRateLimiter;
import com.upmudoum.trade.domain.kis.repository.KisOpenApiTokenRepository;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class KisOpenApiTokenServiceTests {

    @Autowired
    private KisOpenApiTokenRepository repository;

    @Test
    void issueOpenApiTokenReadsPersistedTokenWithoutExternalIssue() {
        KisOpenApiTokenService service = new KisOpenApiTokenService(repository, client());
        repository.save(token(KisTradeMode.PAPER, "persisted-token", Instant.now(), Instant.now().plusSeconds(3600)));

        assertThat(service.issueOpenApiToken(KisTradeMode.PAPER)).isEqualTo("persisted-token");
    }

    @Test
    void issueOpenApiTokenRejectsMissingPersistedToken() {
        KisOpenApiTokenService service = new KisOpenApiTokenService(repository, client());

        assertThatThrownBy(() -> service.issueOpenApiToken(KisTradeMode.PAPER))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not issued yet");
    }

    private KisOpenApiToken token(KisTradeMode tradeMode, String accessToken, Instant issuedAt, Instant expiresAt) {
        KisOpenApiToken token = new KisOpenApiToken();
        token.setTradeMode(tradeMode);
        token.setAccessToken(accessToken);
        token.setIssuedAt(issuedAt);
        token.setUpdatedAt(issuedAt);
        token.setExpiresAt(expiresAt);
        return token;
    }

    private KisOpenApiTokenClient client() {
        return new KisOpenApiTokenClient(
                new ObjectMapper(),
                new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01"),
                new KisRateLimiter(),
                "/oauth2/tokenP"
        );
    }
}
