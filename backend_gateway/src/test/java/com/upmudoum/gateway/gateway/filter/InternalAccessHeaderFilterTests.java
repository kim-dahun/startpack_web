package com.upmudoum.gateway.gateway.filter;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class InternalAccessHeaderFilterTests {

    @Test
    void addsUnifiedInternalGatewayHeadersToDownstreamRequest() throws Exception {
        GatewayProperties properties = new GatewayProperties();
        properties.getInternal().setGatewayId("gateway-instance-a");
        properties.getInternal().setGatewaySecret("secret-a");
        InternalAccessHeaderFilter filter = new InternalAccessHeaderFilter(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(GatewayHeaders.INTERNAL_GATEWAY_ID, "client-spoofed-id");
        request.addHeader(GatewayHeaders.INTERNAL_GATEWAY_SECRET, "client-spoofed-secret");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.INTERNAL_GATEWAY_ID))
                .isEqualTo("gateway-instance-a");
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.INTERNAL_GATEWAY_SECRET))
                .isEqualTo("secret-a");
    }
}
