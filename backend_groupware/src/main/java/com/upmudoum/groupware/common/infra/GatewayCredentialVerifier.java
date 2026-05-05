package com.upmudoum.groupware.common.infra;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GatewayCredentialVerifier {

    public static final String GATEWAY_ID_HEADER = "X-Internal-Gateway-Id";
    public static final String GATEWAY_SECRET_HEADER = "X-Internal-Gateway-Secret";

    private final Map<String, String> allowedGateways;

    public GatewayCredentialVerifier(
            @Value("${groupware.gateway-only.allowed-gateways:backend_gateway:local-dev-gateway-secret}") String allowedGateways,
            @Value("${groupware.gateway-only.allowed-gateway-ids:backend_gateway}") String allowedGatewayIds,
            @Value("${groupware.gateway-only.shared-secret:local-dev-gateway-secret}") String sharedSecret) {
        this.allowedGateways = parseAllowedGateways(allowedGateways, allowedGatewayIds, sharedSecret);
    }

    public boolean isAllowed(String gatewayId, String gatewaySecret) {
        String expectedSecret = gatewayId == null ? null : allowedGateways.get(gatewayId);
        return hasText(expectedSecret) && constantTimeEquals(expectedSecret, gatewaySecret);
    }

    private Map<String, String> parseAllowedGateways(String gatewayCredentials, String gatewayIds, String sharedSecret) {
        Map<String, String> parsed = Arrays.stream(gatewayCredentials.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.split(":", 2))
                .filter(parts -> parts.length == 2 && hasText(parts[0]) && hasText(parts[1]))
                .collect(Collectors.toUnmodifiableMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim(),
                        (first, second) -> second));
        if (!parsed.isEmpty()) {
            return parsed;
        }

        if (!hasText(sharedSecret)) {
            return Map.of();
        }
        return Arrays.stream(gatewayIds.split(","))
                .map(String::trim)
                .filter(this::hasText)
                .collect(Collectors.toUnmodifiableMap(
                        gatewayId -> gatewayId,
                        gatewayId -> sharedSecret,
                        (first, second) -> second));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean constantTimeEquals(String expected, String actual) {
        if (actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
