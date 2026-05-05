package com.upmudoum.auth.common.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.auth.common.api.ApiResponse;
import com.upmudoum.auth.common.api.ErrorResponseData;
import com.upmudoum.auth.config.security.AuthGatewayAccessProperties;
import com.upmudoum.auth.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class GatewayOnlyRequestFilter extends OncePerRequestFilter {

    private static final List<String> AUTH_PATH_PATTERNS = List.of("/api/auth/**", "/api/v1/auth/**");

    private final ObjectMapper objectMapper;
    private final AuthGatewayAccessProperties gatewayAccessProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public GatewayOnlyRequestFilter(
            ObjectMapper objectMapper,
            AuthGatewayAccessProperties gatewayAccessProperties
    ) {
        this.objectMapper = objectMapper;
        this.gatewayAccessProperties = gatewayAccessProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isProtectedAuthPath(request.getRequestURI()) || isAllowedGatewayRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        ErrorCode errorCode = ErrorCode.GATEWAY_ACCESS_REQUIRED;
        response.setStatus(errorCode.status().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.failure(
                        errorCode.code(),
                        errorCode.message(),
                        new ErrorResponseData(Instant.now(), request.getRequestURI())
                )
        );
    }

    private boolean isProtectedAuthPath(String requestUri) {
        return AUTH_PATH_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    private boolean isAllowedGatewayRequest(HttpServletRequest request) {
        String gatewayId = request.getHeader(gatewayAccessProperties.getGatewayIdHeaderName());
        String gatewaySecret = request.getHeader(gatewayAccessProperties.getGatewaySecretHeaderName());
        if (!StringUtils.hasText(gatewayId) || !StringUtils.hasText(gatewaySecret)) {
            return false;
        }
        return gatewayAccessProperties.isAllowed(gatewayId, gatewaySecret);
    }
}
