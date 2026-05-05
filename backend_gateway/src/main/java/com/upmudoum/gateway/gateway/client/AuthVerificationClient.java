package com.upmudoum.gateway.gateway.client;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import com.upmudoum.gateway.exception.AuthServerUnavailableException;
import com.upmudoum.gateway.exception.GatewayAuthException;
import com.upmudoum.gateway.gateway.client.dto.AuthApiResponse;
import com.upmudoum.gateway.gateway.client.dto.AuthRefreshResponse;
import com.upmudoum.gateway.gateway.client.dto.TokenType;
import com.upmudoum.gateway.gateway.client.dto.TokenVerifyRequest;
import com.upmudoum.gateway.gateway.client.dto.TokenVerifyResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class AuthVerificationClient {

    private static final ParameterizedTypeReference<AuthApiResponse<TokenVerifyResponse>> TOKEN_VERIFY_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };
    private static final String COOKIE_REFRESH_REQUEST_BODY = "{}";

    private final RestClient restClient;
    private final GatewayProperties properties;
    private final CircuitBreaker authVerificationCircuitBreaker;
    private final Map<String, CachedToken> verifiedTokenCache = new ConcurrentHashMap<>();

    @Autowired
    public AuthVerificationClient(
            RestClient.Builder restClientBuilder,
            GatewayProperties properties,
            CircuitBreaker authVerificationCircuitBreaker
    ) {
        this.properties = properties;
        this.authVerificationCircuitBreaker = authVerificationCircuitBreaker;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getAuth().getTimeout());
        requestFactory.setReadTimeout(properties.getAuth().getTimeout());
        this.restClient = restClientBuilder.requestFactory(requestFactory).build();
    }

    AuthVerificationClient(
            RestClient restClient,
            GatewayProperties properties,
            CircuitBreaker authVerificationCircuitBreaker
    ) {
        this.properties = properties;
        this.authVerificationCircuitBreaker = authVerificationCircuitBreaker;
        this.restClient = restClient;
    }

    public TokenVerifyResponse verify(String token, TokenType tokenType) {
        TokenVerifyResponse cachedToken = getCachedToken(token, tokenType);
        if (cachedToken != null) {
            return cachedToken;
        }

        URI verifyUri = properties.getRoutes().getServices().getAuthUrl().resolve(properties.getAuth().getVerifyPath());
        AuthApiResponse<TokenVerifyResponse> response;

        try {
            Supplier<AuthApiResponse<TokenVerifyResponse>> verifyCall =
                    CircuitBreaker.decorateSupplier(authVerificationCircuitBreaker, () -> requestVerification(verifyUri, token, tokenType));
            response = verifyCall.get();
        } catch (CallNotPermittedException ex) {
            throw new GatewayAuthException("AUTH_CIRCUIT_OPEN", "auth verification circuit breaker is open.");
        } catch (AuthServerUnavailableException ex) {
            throw new GatewayAuthException("AUTH_SERVER_UNAVAILABLE", "auth verification service is unavailable.");
        }

        if (response == null || !response.isSuccess() || response.getData() == null || !response.getData().isValid()) {
            throw new GatewayAuthException("INVALID_TOKEN", "token verification failed.");
        }
        putCachedToken(token, tokenType, response.getData());
        return response.getData();
    }

    public AuthRefreshResponse refresh(String cookieHeader) {
        URI refreshUri = properties.getRoutes().getServices().getAuthUrl().resolve(properties.getAuth().getRefreshPath());
        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(refreshUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.COOKIE, cookieHeader)
                    .header(GatewayHeaders.INTERNAL_GATEWAY_ID, properties.getInternal().getGatewayId())
                    .header(GatewayHeaders.INTERNAL_GATEWAY_SECRET, properties.getInternal().getGatewaySecret())
                    .body(COOKIE_REFRESH_REQUEST_BODY)
                    .retrieve()
                    .toEntity(String.class);
            List<String> setCookieHeaders = response.getHeaders().getOrEmpty(HttpHeaders.SET_COOKIE);
            String accessToken = extractCookieValue(setCookieHeaders, properties.getAuth().getAccessTokenCookieName());
            if (!response.getStatusCode().is2xxSuccessful() || accessToken == null) {
                throw new GatewayAuthException("INVALID_TOKEN", "token refresh failed.");
            }
            return new AuthRefreshResponse(accessToken, setCookieHeaders);
        } catch (GatewayAuthException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                throw new GatewayAuthException("INVALID_TOKEN", "token refresh failed.");
            }
            throw new GatewayAuthException("AUTH_SERVER_UNAVAILABLE", "auth refresh service is unavailable.");
        } catch (RestClientException ex) {
            throw new GatewayAuthException("AUTH_SERVER_UNAVAILABLE", "auth refresh service is unavailable.");
        }
    }

    private AuthApiResponse<TokenVerifyResponse> requestVerification(URI verifyUri, String token, TokenType tokenType) {
        try {
            return restClient.post()
                    .uri(verifyUri)
                    .header(GatewayHeaders.INTERNAL_GATEWAY_ID, properties.getInternal().getGatewayId())
                    .header(GatewayHeaders.INTERNAL_GATEWAY_SECRET, properties.getInternal().getGatewaySecret())
                    .body(new TokenVerifyRequest(token, tokenType))
                    .retrieve()
                    .body(TOKEN_VERIFY_RESPONSE_TYPE);
        } catch (RestClientException ex) {
            throw new AuthServerUnavailableException(ex);
        }
    }

    private String extractCookieValue(List<String> setCookieHeaders, String cookieName) {
        String cookiePrefix = cookieName + "=";
        return setCookieHeaders.stream()
                .map(header -> header.split(";", 2)[0])
                .filter(cookie -> cookie.startsWith(cookiePrefix))
                .map(cookie -> cookie.substring(cookiePrefix.length()))
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse(null);
    }

    private TokenVerifyResponse getCachedToken(String token, TokenType tokenType) {
        if (!properties.getAuth().getCache().isEnabled()) {
            return null;
        }
        CachedToken cachedToken = verifiedTokenCache.get(cacheKey(token, tokenType));
        if (cachedToken == null || cachedToken.isExpired()) {
            return null;
        }
        return cachedToken.getToken();
    }

    private void putCachedToken(String token, TokenType tokenType, TokenVerifyResponse verifiedToken) {
        if (!properties.getAuth().getCache().isEnabled()) {
            return;
        }
        if (verifiedTokenCache.size() >= properties.getAuth().getCache().getMaxSize()) {
            verifiedTokenCache.clear();
        }
        Instant expiresAt = Instant.now().plus(properties.getAuth().getCache().getTtl());
        verifiedTokenCache.put(cacheKey(token, tokenType), new CachedToken(verifiedToken, expiresAt));
    }

    private String cacheKey(String token, TokenType tokenType) {
        return tokenType.name() + ":" + token;
    }

    private static class CachedToken {

        private final TokenVerifyResponse token;
        private final Instant expiresAt;

        CachedToken(TokenVerifyResponse token, Instant expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }

        TokenVerifyResponse getToken() {
            return token;
        }

        boolean isExpired() {
            return !Instant.now().isBefore(expiresAt);
        }
    }
}
