package com.upmudoum.gateway.gateway.filter;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.common.web.MutableHeaderHttpServletRequest;
import com.upmudoum.gateway.config.GatewayProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 25)
public class InternalAccessHeaderFilter extends OncePerRequestFilter {

    private final GatewayProperties properties;

    public InternalAccessHeaderFilter(GatewayProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        GatewayProperties.Internal internal = properties.getInternal();

        MutableHeaderHttpServletRequest wrappedRequest = new MutableHeaderHttpServletRequest(request);
        wrappedRequest.putHeader(GatewayHeaders.INTERNAL_GATEWAY_ID, internal.getGatewayId());
        wrappedRequest.putHeader(GatewayHeaders.INTERNAL_GATEWAY_SECRET, internal.getGatewaySecret());

        filterChain.doFilter(wrappedRequest, response);
    }
}
