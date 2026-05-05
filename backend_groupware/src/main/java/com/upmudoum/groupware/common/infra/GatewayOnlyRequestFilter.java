package com.upmudoum.groupware.common.infra;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GatewayOnlyRequestFilter extends OncePerRequestFilter {

    private final boolean enabled;
    private final GatewayCredentialVerifier gatewayCredentialVerifier;

    public GatewayOnlyRequestFilter(
            @Value("${groupware.gateway-only.enabled:true}") boolean enabled,
            GatewayCredentialVerifier gatewayCredentialVerifier) {
        this.enabled = enabled;
        this.gatewayCredentialVerifier = gatewayCredentialVerifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!enabled || isGatewayRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"status":403,"message":"Groupware requests must pass through gateway"}
                """);
    }

    private boolean isGatewayRequest(HttpServletRequest request) {
        String gatewayId = request.getHeader(GatewayCredentialVerifier.GATEWAY_ID_HEADER);
        String gatewaySecret = request.getHeader(GatewayCredentialVerifier.GATEWAY_SECRET_HEADER);
        return gatewayCredentialVerifier.isAllowed(gatewayId, gatewaySecret);
    }
}
