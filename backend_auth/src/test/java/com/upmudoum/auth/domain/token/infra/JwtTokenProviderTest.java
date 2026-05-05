package com.upmudoum.auth.domain.token.infra;

import com.upmudoum.auth.config.security.AuthJwtProperties;
import com.upmudoum.auth.domain.token.vo.TokenIssueResult;
import com.upmudoum.auth.domain.token.vo.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    @Test
    void issueAccessTokenWritesExpectedClaims() {
        AuthJwtProperties properties = createProperties();
        JwtTokenProvider provider = new JwtTokenProvider(properties);

        TokenIssueResult result = provider.issueAccessToken("user-100", List.of("USER"));

        Claims claims = Jwts.parser()
                .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        properties.getAccess().getSecret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(result.getToken())
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("user-100");
        assertThat(claims.getIssuer()).isEqualTo("backend-auth");
        assertThat(claims.get("typ", String.class)).isEqualTo("ACCESS");
        assertThat(claims.get("roles", List.class)).containsExactly("USER");
        assertThat(result.getExpiresAt()).isAfter(result.getIssuedAt());
    }

    @Test
    void issueOpenApiTokenWritesScopesClaim() {
        AuthJwtProperties properties = createProperties();
        JwtTokenProvider provider = new JwtTokenProvider(properties);

        TokenIssueResult result = provider.issueOpenApiToken("system-trader", List.of("trade.read"));

        Claims claims = Jwts.parser()
                .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        properties.getOpenApi().getSecret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(result.getToken())
                .getPayload();

        assertThat(claims.get("typ", String.class)).isEqualTo("OPEN_API");
        assertThat(claims.get("scopes", List.class)).containsExactly("trade.read");
    }

    @Test
    void verifyExpiredTokenUsesUnifiedExpiredCode() {
        AuthJwtProperties properties = createProperties();
        properties.getAccess().setTtl(Duration.ofSeconds(-31));
        JwtTokenProvider provider = new JwtTokenProvider(properties);

        TokenIssueResult token = provider.issueAccessToken("user-100", List.of("USER"));

        assertThatThrownBy(() -> provider.verifyToken(token.getToken(), TokenType.ACCESS))
                .isInstanceOf(com.upmudoum.auth.exception.ApiException.class)
                .hasMessage(com.upmudoum.auth.exception.ErrorCode.TOKEN_EXPIRED.message());
    }

    private AuthJwtProperties createProperties() {
        AuthJwtProperties properties = new AuthJwtProperties();
        properties.setIssuer("backend-auth");
        properties.setClockSkew(Duration.ofSeconds(30));

        AuthJwtProperties.TokenSpec access = new AuthJwtProperties.TokenSpec();
        access.setAudience("backend-user");
        access.setSecret("change-me-access-secret-key-1234567890");
        access.setTtl(Duration.ofMinutes(15));

        AuthJwtProperties.TokenSpec refresh = new AuthJwtProperties.TokenSpec();
        refresh.setAudience("backend-user");
        refresh.setSecret("change-me-refresh-secret-key-1234567890");
        refresh.setTtl(Duration.ofDays(14));

        AuthJwtProperties.TokenSpec openApi = new AuthJwtProperties.TokenSpec();
        openApi.setAudience("backend-gateway");
        openApi.setSecret("change-me-open-api-secret-key-1234567890");
        openApi.setTtl(Duration.ofMinutes(10));

        properties.setAccess(access);
        properties.setRefresh(refresh);
        properties.setOpenApi(openApi);
        return properties;
    }
}
