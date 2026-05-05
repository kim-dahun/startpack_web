package com.upmudoum.trade.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.socket.handler.TextWebSocketHandler;

class GatewayOnlyHandshakeInterceptorTests {

    @Test
    void allowsGatewayBridgeHeadersAndStoresGatewayId() {
        GatewayOnlyHandshakeInterceptor interceptor = new GatewayOnlyHandshakeInterceptor(properties());
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("X-Internal-Gateway-Id", "backend_gateway");
        servletRequest.addHeader("X-Internal-Gateway-Secret", "local-secret");
        Map<String, Object> attributes = new HashMap<>();

        boolean allowed = interceptor.beforeHandshake(
                request(servletRequest),
                response(new MockHttpServletResponse()),
                new TextWebSocketHandler(),
                attributes
        );

        assertThat(allowed).isTrue();
        assertThat(attributes).containsEntry("gatewayId", "backend_gateway");
    }

    @Test
    void rejectsBrowserDirectHandshakeWithoutGatewayHeaders() {
        GatewayOnlyHandshakeInterceptor interceptor = new GatewayOnlyHandshakeInterceptor(properties());
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        boolean allowed = interceptor.beforeHandshake(
                request(new MockHttpServletRequest()),
                response(servletResponse),
                new TextWebSocketHandler(),
                new HashMap<>()
        );

        assertThat(allowed).isFalse();
        assertThat(servletResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    private GatewayAccessProperties properties() {
        GatewayAccessProperties properties = new GatewayAccessProperties();
        properties.setEnforce(true);
        GatewayAccessProperties.AllowedGateway gateway = new GatewayAccessProperties.AllowedGateway();
        gateway.setGatewayId("backend_gateway");
        gateway.setSharedSecret("local-secret");
        properties.setAllowedGateways(List.of(gateway));
        return properties;
    }

    private ServerHttpRequest request(MockHttpServletRequest request) {
        return new ServletServerHttpRequest(request);
    }

    private ServerHttpResponse response(MockHttpServletResponse response) {
        return new ServletServerHttpResponse(response);
    }
}
