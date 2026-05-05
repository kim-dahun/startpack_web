package com.upmudoum.gateway.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
public class RouteReadinessValidator implements ApplicationRunner {

    private final GatewayProperties properties;

    public RouteReadinessValidator(GatewayProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        GatewayProperties.Services services = properties.getRoutes().getServices();
        validateServiceUri("auth", services.getAuthUrl());
        validateServiceUri("user", services.getUserUrl());
        validateServiceUri("erp", services.getErpUrl());
        validateServiceUri("groupware", services.getGroupwareUrl());
        validateServiceUri("trade", services.getTradeUrl());
        validatePaths("gateway.routes.public-paths", properties.getRoutes().getPublicPaths());
        validatePaths("gateway.routes.internal-paths", properties.getRoutes().getInternalPaths());
        validatePaths("gateway.routes.open-api-paths", properties.getRoutes().getOpenApiPaths());
        validateAuthSettings();
        validateCorsSettings();
        validateInternalSettings();
        validateWebsocketSettings();
        validateLoggingSettings();
        validateAuthorizationSettings();
    }

    private void validateServiceUri(String serviceName, URI uri) {
        String scheme = uri.getScheme();
        if ((!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) || uri.getHost() == null) {
            throw new IllegalStateException("Invalid gateway route URI for " + serviceName + ": " + uri);
        }
    }

    private void validatePaths(String key, List<String> paths) {
        if (paths.isEmpty()) {
            throw new IllegalStateException(key + " must contain at least one path");
        }
    }

    private void validateAuthSettings() {
        String verifyPath = properties.getAuth().getVerifyPath();
        if (verifyPath == null || !verifyPath.startsWith("/")) {
            throw new IllegalStateException("gateway.auth.verify-path must start with '/'");
        }
        String refreshPath = properties.getAuth().getRefreshPath();
        if (refreshPath == null || !refreshPath.startsWith("/")) {
            throw new IllegalStateException("gateway.auth.refresh-path must start with '/'");
        }
        if (properties.getAuth().getAccessTokenCookieName() == null
                || properties.getAuth().getAccessTokenCookieName().isBlank()) {
            throw new IllegalStateException("gateway.auth.access-token-cookie-name is required");
        }
        if (properties.getAuth().getRefreshTokenCookieName() == null
                || properties.getAuth().getRefreshTokenCookieName().isBlank()) {
            throw new IllegalStateException("gateway.auth.refresh-token-cookie-name is required");
        }
        if (properties.getAuth().getTimeout().isNegative() || properties.getAuth().getTimeout().isZero()) {
            throw new IllegalStateException("gateway.auth.timeout must be greater than zero");
        }
        if (properties.getAuth().getCache().getTtl().isNegative() || properties.getAuth().getCache().getTtl().isZero()) {
            throw new IllegalStateException("gateway.auth.cache.ttl must be greater than zero");
        }
        if (properties.getAuth().getCache().getMaxSize() < 1) {
            throw new IllegalStateException("gateway.auth.cache.max-size must be greater than zero");
        }
        GatewayProperties.CircuitBreaker circuitBreaker = properties.getAuth().getCircuitBreaker();
        if (circuitBreaker.getName() == null || circuitBreaker.getName().isBlank()) {
            throw new IllegalStateException("gateway.auth.circuit-breaker.name is required");
        }
        if (circuitBreaker.getFailureRateThreshold() <= 0 || circuitBreaker.getFailureRateThreshold() > 100) {
            throw new IllegalStateException("gateway.auth.circuit-breaker.failure-rate-threshold must be 0 < n <= 100");
        }
        if (circuitBreaker.getSlidingWindowSize() < 2) {
            throw new IllegalStateException("gateway.auth.circuit-breaker.sliding-window-size must be at least 2");
        }
        if (circuitBreaker.getPermittedNumberOfCallsInHalfOpenState() < 1) {
            throw new IllegalStateException("gateway.auth.circuit-breaker.permitted-number-of-calls-in-half-open-state must be greater than zero");
        }
        if (circuitBreaker.getWaitDurationInOpenState().isNegative()
                || circuitBreaker.getWaitDurationInOpenState().isZero()) {
            throw new IllegalStateException("gateway.auth.circuit-breaker.wait-duration-in-open-state must be greater than zero");
        }
    }

    private void validateCorsSettings() {
        validatePaths("gateway.cors.allowed-origins", properties.getCors().getAllowedOrigins());
        validatePaths("gateway.cors.allowed-methods", properties.getCors().getAllowedMethods());
        validatePaths("gateway.cors.allowed-headers", properties.getCors().getAllowedHeaders());
        if (properties.getCors().getMaxAge().isNegative() || properties.getCors().getMaxAge().isZero()) {
            throw new IllegalStateException("gateway.cors.max-age must be greater than zero");
        }
    }

    private void validateLoggingSettings() {
        if (properties.getLogging().getSlowRequestThresholdMillis() < 0) {
            throw new IllegalStateException("gateway.logging.slow-request-threshold-millis must be zero or greater");
        }
    }

    private void validateInternalSettings() {
        if (properties.getInternal().getGatewayId() == null || properties.getInternal().getGatewayId().isBlank()) {
            throw new IllegalStateException("gateway.internal.gateway-id is required");
        }
        if (properties.getInternal().getGatewaySecret() == null || properties.getInternal().getGatewaySecret().isBlank()) {
            throw new IllegalStateException("gateway.internal.gateway-secret is required");
        }
    }

    private void validateWebsocketSettings() {
        GatewayProperties.Websocket websocket = properties.getWebsocket();
        if (websocket.getTradeRealtimePath() == null || !websocket.getTradeRealtimePath().startsWith("/")) {
            throw new IllegalStateException("gateway.websocket.trade-realtime-path must start with '/'");
        }
        if (websocket.getTradeRealtimeBackendPath() == null || !websocket.getTradeRealtimeBackendPath().startsWith("/")) {
            throw new IllegalStateException("gateway.websocket.trade-realtime-backend-path must start with '/'");
        }
        if (websocket.getConnectTimeout().isNegative() || websocket.getConnectTimeout().isZero()) {
            throw new IllegalStateException("gateway.websocket.connect-timeout must be greater than zero");
        }
    }

    private void validateAuthorizationSettings() {
        for (GatewayProperties.Rule rule : properties.getAuthorization().getRules()) {
            if (rule.getPathPattern() == null || rule.getPathPattern().isBlank()) {
                throw new IllegalStateException("gateway.authorization.rules[].path-pattern is required");
            }
            if (rule.getRequiredRoles().isEmpty()) {
                throw new IllegalStateException("gateway.authorization.rules[].required-roles must contain at least one role");
            }
        }
    }
}
