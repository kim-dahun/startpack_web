package com.upmudoum.trade.domain.kis.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;

class KisOpenApiTokenClientTests {

    @Test
    void extractTokenReadsKisAccessTokenResponse() {
        KisOpenApiTokenClient client = client();

        KisOpenApiTokenClient.IssuedToken token = client.extractToken(Map.of(
                "access_token", "kis-access-token",
                "access_token_token_expired", "2999-12-31 23:59:59",
                "token_type", "Bearer"
        ));

        assertThat(token.getAccessToken()).isEqualTo("kis-access-token");
        assertThat(token.getExpiresAt()).isAfter(java.time.Instant.now());
    }

    @Test
    void extractTokenSupportsExpiresInFallback() {
        KisOpenApiTokenClient client = client();

        KisOpenApiTokenClient.IssuedToken token = client.extractToken(Map.of(
                "access_token", "kis-access-token",
                "expires_in", 3600
        ));

        assertThat(token.getAccessToken()).isEqualTo("kis-access-token");
        assertThat(token.getExpiresAt()).isAfter(java.time.Instant.now());
    }

    @Test
    void extractTokenRejectsMissingAccessToken() {
        KisOpenApiTokenClient client = client();

        assertThatThrownBy(() -> client.extractToken(Map.of("rt_cd", "0")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("KIS open-api token response");
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
