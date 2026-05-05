package com.upmudoum.trade.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiLoggingInterceptor.class);
    private static final String STARTED_AT = "apiLogStartedAt";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(STARTED_AT, System.currentTimeMillis());
        putMdc("requestId", firstNonBlank(request.getHeader("X-Request-Id"), UUID.randomUUID().toString()));
        putMdc("userId", firstNonBlank(request.getHeader("X-User-Id"), request.getHeader("X-Internal-User-Id")));
        putMdc("companyId", firstNonBlank(request.getHeader("X-Company-Id"), request.getHeader("X-Internal-Company-Id")));
        log.info("API_REQUEST domain={} method={} path={} query={}", domain(request.getRequestURI()), request.getMethod(), request.getRequestURI(), sanitize(request.getQueryString()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startedAt = (long) request.getAttribute(STARTED_AT);
        long duration = System.currentTimeMillis() - startedAt;
        if (ex == null) {
            log.info("API_RESULT domain={} method={} path={} status={} durationMs={}", domain(request.getRequestURI()), request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
        } else {
            log.error("API_RESULT domain={} method={} path={} status={} durationMs={} exception={}", domain(request.getRequestURI()), request.getMethod(), request.getRequestURI(), response.getStatus(), duration, ex.getClass().getSimpleName(), ex);
        }
        MDC.clear();
    }

    private void putMdc(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private String sanitize(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return "";
        }
        return queryString.replaceAll("(?i)(token|secret|password|appkey|appsecret)=([^&]*)", "$1=***");
    }

    private String domain(String path) {
        if (path.contains("/accounts")) {
            return "account";
        }
        if (path.contains("/watchlist")) {
            return "watchlist";
        }
        if (path.contains("/histories")) {
            return "trade";
        }
        if (path.contains("/realtime")) {
            return "marketdata";
        }
        if (path.contains("/items")) {
            return "item";
        }
        if (path.contains("/kis")) {
            return "kis";
        }
        return "unknown";
    }
}
