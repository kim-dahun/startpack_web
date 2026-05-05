package com.upmudoum.groupware.common.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class GatewayOnlyRequestFilterTests {

    @Test
    void allowsConfiguredGatewayIdAndSecret() throws Exception {
        GatewayOnlyRequestFilter filter = new GatewayOnlyRequestFilter(
                true,
                new GatewayCredentialVerifier(
                        "backend_gateway:local-dev-gateway-secret,backend_gateway_blue:blue-secret",
                        "backend_gateway,backend_gateway_blue",
                        "local-dev-gateway-secret"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader("X-Internal-Gateway-Id", "backend_gateway_blue");
        request.addHeader("X-Internal-Gateway-Secret", "blue-secret");

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isSameAs(request);
    }

    @Test
    void rejectsRequestsWithoutInternalGatewayHeaders() throws Exception {
        GatewayOnlyRequestFilter filter = new GatewayOnlyRequestFilter(
                true,
                new GatewayCredentialVerifier(
                        "backend_gateway:local-dev-gateway-secret",
                        "backend_gateway",
                        "local-dev-gateway-secret"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("Groupware requests must pass through gateway");
    }

    @Test
    void rejectsUnknownGatewayIdOrWrongSecret() throws Exception {
        GatewayOnlyRequestFilter filter = new GatewayOnlyRequestFilter(
                true,
                new GatewayCredentialVerifier(
                        "backend_gateway:local-dev-gateway-secret",
                        "backend_gateway",
                        "local-dev-gateway-secret"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("X-Internal-Gateway-Id", "unknown_gateway");
        request.addHeader("X-Internal-Gateway-Secret", "local-dev-gateway-secret");

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void fallsBackToSharedSecretForLegacyAllowedGatewayIds() throws Exception {
        GatewayOnlyRequestFilter filter = new GatewayOnlyRequestFilter(
                true,
                new GatewayCredentialVerifier(
                        "",
                        "backend_gateway,backend_gateway_blue",
                        "local-dev-gateway-secret"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.addHeader("X-Internal-Gateway-Id", "backend_gateway_blue");
        request.addHeader("X-Internal-Gateway-Secret", "local-dev-gateway-secret");

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isSameAs(request);
    }
}
