package com.upmudoum.gateway.gateway.filter;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.common.web.MutableHeaderHttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        response.setHeader(GatewayHeaders.REQUEST_ID, requestId);
        MDC.put("requestId", requestId);

        try {
            MutableHeaderHttpServletRequest wrappedRequest = new MutableHeaderHttpServletRequest(request);
            wrappedRequest.putHeader(GatewayHeaders.REQUEST_ID, requestId);
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            MDC.remove("requestId");
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String headerValue = request.getHeader(GatewayHeaders.REQUEST_ID);
        if (StringUtils.hasText(headerValue)) {
            return headerValue;
        }
        return UUID.randomUUID().toString();
    }
}
