package com.upmudoum.erp.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

public class ApiAccessLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ApiAccessLoggingInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = ApiAccessLoggingInterceptor.class.getName() + ".startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        putMdc("requestId", firstHeader(request, "X-Request-Id", "X-Correlation-Id"));
        putMdc("userId", firstHeader(request, "X-User-Id"));
        putMdc("companyCode", firstHeader(request, "X-Com-Cd", "X-Company-Code"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long durationMillis = durationMillis(request);
        int status = response.getStatus();
        String result = status >= 500 ? "ERROR" : status >= 400 ? "FAIL" : "SUCCESS";
        if (ex == null) {
            log.info("api result={} method={} path={} status={} durationMs={}",
                    result, request.getMethod(), request.getRequestURI(), status, durationMillis);
        } else {
            log.error("api result={} method={} path={} status={} durationMs={}",
                    result, request.getMethod(), request.getRequestURI(), status, durationMillis, ex);
        }
        MDC.clear();
    }

    private long durationMillis(HttpServletRequest request) {
        Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime instanceof Long value) {
            return System.currentTimeMillis() - value;
        }
        return -1L;
    }

    private void putMdc(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }

    private String firstHeader(HttpServletRequest request, String... headerNames) {
        for (String headerName : headerNames) {
            String value = request.getHeader(headerName);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
