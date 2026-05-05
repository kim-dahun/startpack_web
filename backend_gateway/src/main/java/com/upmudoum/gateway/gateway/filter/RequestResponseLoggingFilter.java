package com.upmudoum.gateway.gateway.filter;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    private final GatewayProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RequestResponseLoggingFilter(GatewayProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!properties.getLogging().isRequestResponseEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        long startedAt = System.nanoTime();
        Exception failure = null;
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            failure = ex;
            throw ex;
        } finally {
            long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
            logRequest(request, response, elapsedMillis, failure);
        }
    }

    private void logRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            long elapsedMillis,
        Exception failure
    ) {
        String requestId = request.getHeader(GatewayHeaders.REQUEST_ID);
        String downstream = resolveDownstream(request.getRequestURI());
        List<String> cookieNames = resolveCookieNames(request);
        String message = "gateway request method={} path={} downstream={} status={} durationMs={} requestId={} clientIp={} cookieNames={}";
        if (failure != null || response.getStatus() >= 500) {
            log.error(message, request.getMethod(), request.getRequestURI(), downstream, response.getStatus(),
                    elapsedMillis, requestId, request.getRemoteAddr(), cookieNames, failure);
            return;
        }
        if (elapsedMillis >= properties.getLogging().getSlowRequestThresholdMillis()) {
            log.warn(message, request.getMethod(), request.getRequestURI(), downstream, response.getStatus(),
                    elapsedMillis, requestId, request.getRemoteAddr(), cookieNames);
            return;
        }
        log.info(message, request.getMethod(), request.getRequestURI(), downstream, response.getStatus(),
                elapsedMillis, requestId, request.getRemoteAddr(), cookieNames);
    }

    private List<String> resolveCookieNames(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return List.of();
        }
        return Arrays.stream(request.getCookies())
                .map(cookie -> cookie.getName())
                .toList();
    }

    private String resolveDownstream(String requestPath) {
        GatewayProperties.Services services = properties.getRoutes().getServices();
        if (pathMatcher.match("/api/auth/**", requestPath)
                || pathMatcher.match("/api/v1/auth/**", requestPath)) {
            return formatDownstream("auth", services.getAuthUrl());
        }
        if (pathMatcher.match("/api/users/**", requestPath)) {
            return formatDownstream("user", services.getUserUrl());
        }
        if (pathMatcher.match("/api/erp/**", requestPath)) {
            return formatDownstream("erp", services.getErpUrl());
        }
        if (pathMatcher.match("/api/groupware/**", requestPath)) {
            return formatDownstream("groupware", services.getGroupwareUrl());
        }
        if (pathMatcher.match("/api/trade/**", requestPath)) {
            return formatDownstream("trade", services.getTradeUrl());
        }
        return "local";
    }

    private String formatDownstream(String serviceName, URI uri) {
        return serviceName + ":" + uri;
    }
}
