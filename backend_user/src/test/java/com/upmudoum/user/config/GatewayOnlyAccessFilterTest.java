package com.upmudoum.user.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class GatewayOnlyAccessFilterTest {

    @Test
    void rejectsRequestWithoutGatewayHeader() throws ServletException, IOException {
        GatewayOnlyAccessFilter filter = new GatewayOnlyAccessFilter(properties(true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, noOpChain());

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void allowsRequestWithGatewayHeader() throws ServletException, IOException {
        GatewayOnlyAccessFilter filter = new GatewayOnlyAccessFilter(properties(true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("X-Internal-Gateway-Id", "backend_gateway");
        request.addHeader("X-Internal-Gateway-Secret", "local-dev-gateway-secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, noOpChain());

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void rejectsRequestFromUnknownGatewayId() throws ServletException, IOException {
        GatewayOnlyAccessFilter filter = new GatewayOnlyAccessFilter(properties(true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("X-Internal-Gateway-Id", "unknown_gateway");
        request.addHeader("X-Internal-Gateway-Secret", "local-dev-gateway-secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, noOpChain());

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void allowsMultipleConfiguredGateways() throws ServletException, IOException {
        GatewayOnlyAccessFilter filter = new GatewayOnlyAccessFilter(properties(true));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.addHeader("X-Internal-Gateway-Id", "backend_gateway_blue");
        request.addHeader("X-Internal-Gateway-Secret", "blue-secret");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, noOpChain());

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void canBeDisabledForLocalDiagnostics() throws ServletException, IOException {
        GatewayOnlyAccessFilter filter = new GatewayOnlyAccessFilter(properties(false));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, noOpChain());

        assertThat(response.getStatus()).isEqualTo(200);
    }

    private GatewayAccessProperties properties(boolean enabled) {
        GatewayAccessProperties properties = new GatewayAccessProperties();
        properties.setEnabled(enabled);
        properties.setGatewayIdHeaderName("X-Internal-Gateway-Id");
        properties.setGatewaySecretHeaderName("X-Internal-Gateway-Secret");
        properties.setAllowedGateways("backend_gateway:local-dev-gateway-secret,backend_gateway_blue:blue-secret");
        return properties;
    }

    private FilterChain noOpChain() {
        return (request, response) -> {
        };
    }
}
