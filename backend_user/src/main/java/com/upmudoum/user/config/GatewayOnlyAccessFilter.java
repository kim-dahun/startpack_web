package com.upmudoum.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

public class GatewayOnlyAccessFilter extends OncePerRequestFilter {

    private final GatewayAccessProperties properties;

    public GatewayOnlyAccessFilter(GatewayAccessProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !properties.isEnabled();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String gatewayId = header(request, properties.getGatewayIdHeaderName());
        String actual = header(request, properties.getGatewaySecretHeaderName());
        String expected = gatewayId == null || gatewayId.isBlank() ? null : allowedGateways().get(gatewayId);
        if (!matches(expected, actual)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Direct access to backend_user is not allowed.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean matches(String expected, String actual) {
        if (expected == null || expected.isBlank() || actual == null || actual.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private String header(HttpServletRequest request, String headerName) {
        return headerName == null || headerName.isBlank() ? null : request.getHeader(headerName);
    }

    private Map<String, String> allowedGateways() {
        String value = properties.getAllowedGateways();
        if (value == null || value.isBlank()) {
            return Map.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isBlank())
                .map(entry -> entry.split(":", 2))
                .filter(parts -> parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank())
                .collect(Collectors.toUnmodifiableMap(parts -> parts[0].trim(), parts -> parts[1].trim(), (left, ignored) -> left));
    }
}
