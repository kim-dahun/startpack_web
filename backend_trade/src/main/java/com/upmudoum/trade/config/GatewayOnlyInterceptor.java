package com.upmudoum.trade.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GatewayOnlyInterceptor implements HandlerInterceptor {

    private final GatewayAccessProperties properties;

    public GatewayOnlyInterceptor(GatewayAccessProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (properties.isAllowed(
                request.getHeader(properties.getGatewayIdHeaderName()),
                request.getHeader(properties.getGatewaySecretHeaderName())
        )) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
