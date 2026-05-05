package com.upmudoum.groupware.common.infra;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiAccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiAccessLogFilter.class);

    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String COM_CD_HEADER = "X-Com-Cd";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startedAt = System.nanoTime();
        putMdc(request);
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            log.error("API_ERROR method={} path={} status={} durationMs={} comCd={} userId={} requestId={}",
                    request.getMethod(),
                    requestPath(request),
                    response.getStatus(),
                    elapsedMillis(startedAt),
                    headerOrDash(request, COM_CD_HEADER),
                    headerOrDash(request, USER_ID_HEADER),
                    headerOrDash(request, REQUEST_ID_HEADER),
                    ex);
            throw ex;
        } finally {
            log.info("API_RESULT method={} path={} status={} durationMs={} comCd={} userId={} requestId={}",
                    request.getMethod(),
                    requestPath(request),
                    response.getStatus(),
                    elapsedMillis(startedAt),
                    headerOrDash(request, COM_CD_HEADER),
                    headerOrDash(request, USER_ID_HEADER),
                    headerOrDash(request, REQUEST_ID_HEADER));
            MDC.clear();
        }
    }

    private void putMdc(HttpServletRequest request) {
        MDC.put("requestId", headerOrDash(request, REQUEST_ID_HEADER));
        MDC.put("comCd", headerOrDash(request, COM_CD_HEADER));
        MDC.put("userId", headerOrDash(request, USER_ID_HEADER));
    }

    private String requestPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + queryString;
    }

    private String headerOrDash(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000L;
    }
}
