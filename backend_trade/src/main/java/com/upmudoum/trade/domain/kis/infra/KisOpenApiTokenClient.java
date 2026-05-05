package com.upmudoum.trade.domain.kis.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.kis.dto.KisOpenApiTokenRequest;
import com.upmudoum.trade.domain.kis.vo.KisProperties;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KisOpenApiTokenClient {

    private static final DateTimeFormatter KIS_EXPIRES_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final KisProperties kisProperties;
    private final KisRateLimiter rateLimiter;
    private final String tokenPath;

    public KisOpenApiTokenClient(
            ObjectMapper objectMapper,
            KisProperties kisProperties,
            KisRateLimiter rateLimiter,
            @Value("${trade.kis.open-api-token-path:/oauth2/tokenP}") String tokenPath
    ) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
        this.kisProperties = kisProperties;
        this.rateLimiter = rateLimiter;
        this.tokenPath = tokenPath;
    }

    public IssuedToken issueOpenApiToken(KisTradeMode tradeMode) {
        kisProperties.validateCredentials();
        rateLimiter.acquire();
        Object response = restClient.post()
                .uri(kisProperties.getBaseUrl(tradeMode) + tokenPath)
                .body(new KisOpenApiTokenRequest(kisProperties.getAppKey(), kisProperties.getAppSecretKey()))
                .retrieve()
                .body(Object.class);
        return extractToken(response);
    }

    IssuedToken extractToken(Object response) {
        JsonNode root = objectMapper.valueToTree(response);
        String accessToken = firstText(root, "access_token", "accessToken", "token");
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("KIS open-api token response does not contain access_token.");
        }
        Instant expiresAt = parseExpiresAt(root);
        return new IssuedToken(accessToken, expiresAt);
    }

    private Instant parseExpiresAt(JsonNode root) {
        String expiresAtText = firstText(root, "access_token_token_expired", "expires_at", "expiresAt");
        if (expiresAtText != null) {
            return LocalDateTime.parse(expiresAtText, KIS_EXPIRES_AT_FORMATTER)
                    .atZone(KOREA_ZONE)
                    .toInstant();
        }
        long expiresIn = root.path("expires_in").asLong(86400L);
        return Instant.now().plusSeconds(Math.max(60L, expiresIn));
    }

    private String firstText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode field = node.path(fieldName);
            if (!field.isMissingNode() && !field.asText().isBlank()) {
                return field.asText();
            }
        }
        return null;
    }

    public static class IssuedToken {

        private final String accessToken;
        private final Instant expiresAt;

        IssuedToken(String accessToken, Instant expiresAt) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public Instant getExpiresAt() {
            return expiresAt;
        }
    }
}
