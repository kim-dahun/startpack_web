package com.upmudoum.gateway.gateway.client;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import com.upmudoum.gateway.exception.GatewayAuthException;
import com.upmudoum.gateway.gateway.client.dto.AuthRefreshResponse;
import com.upmudoum.gateway.gateway.client.dto.TokenType;
import com.upmudoum.gateway.gateway.client.dto.TokenVerifyResponse;
import com.sun.net.httpserver.HttpServer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class AuthVerificationClientTests {

    @Test
    void verifiedTokenIsCachedWithinTtl() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        AuthVerificationClient client = new AuthVerificationClient(
                restClientBuilder.build(),
                gatewayProperties(),
                CircuitBreaker.ofDefaults("authVerification")
        );

        server.expect(once(), requestTo("http://localhost:18081/api/v1/auth/tokens/verify"))
                .andExpect(method(POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header(GatewayHeaders.INTERNAL_GATEWAY_ID, "gateway-test-1"))
                .andExpect(header(GatewayHeaders.INTERNAL_GATEWAY_SECRET, "gateway-secret-test"))
                .andRespond(withSuccess("""
                        {
                          "success": true,
                          "code": "OK",
                          "message": "success",
                          "data": {
                            "valid": true,
                            "tokenType": "ACCESS",
                            "subject": "user-1",
                            "audience": "COMPANY",
                            "permissions": ["ROLE_USER"]
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        TokenVerifyResponse first = client.verify("valid-token", TokenType.ACCESS);
        TokenVerifyResponse second = client.verify("valid-token", TokenType.ACCESS);

        assertThat(first.getSubject()).isEqualTo("user-1");
        assertThat(second.getSubject()).isEqualTo("user-1");
        server.verify();
    }

    @Test
    void authServerErrorIsMappedToGatewayAuthException() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        AuthVerificationClient client = new AuthVerificationClient(
                restClientBuilder.build(),
                gatewayProperties(),
                CircuitBreaker.ofDefaults("authVerification")
        );

        server.expect(once(), requestTo("http://localhost:18081/api/v1/auth/tokens/verify"))
                .andExpect(method(POST))
                .andExpect(header(GatewayHeaders.INTERNAL_GATEWAY_ID, "gateway-test-1"))
                .andExpect(header(GatewayHeaders.INTERNAL_GATEWAY_SECRET, "gateway-secret-test"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.verify("valid-token", TokenType.ACCESS))
                .isInstanceOf(GatewayAuthException.class)
                .hasMessage("auth verification service is unavailable.");
        server.verify();
    }

    @Test
    void refreshForwardsCookieAndReturnsSetCookieHeaders() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        AuthVerificationClient client = new AuthVerificationClient(
                restClientBuilder.build(),
                gatewayProperties(),
                CircuitBreaker.ofDefaults("authVerification")
        );

        server.expect(once(), requestTo("http://localhost:18081/api/auth/tokens/refresh"))
                .andExpect(method(POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{}"))
                .andExpect(header(HttpHeaders.COOKIE, "ACCESS_TOKEN=expired; REFRESH_TOKEN=refresh"))
                .andExpect(header(GatewayHeaders.INTERNAL_GATEWAY_ID, "gateway-test-1"))
                .andExpect(header(GatewayHeaders.INTERNAL_GATEWAY_SECRET, "gateway-secret-test"))
                .andRespond(withSuccess("""
                        {"success":true,"code":"OK","message":"success","data":{"tokenDeliveryMethod":"COOKIE"}}
                        """, MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.SET_COOKIE, "ACCESS_TOKEN=new-access; Path=/; HttpOnly; SameSite=Lax")
                        .header(HttpHeaders.SET_COOKIE, "REFRESH_TOKEN=new-refresh; Path=/; HttpOnly; SameSite=Lax"));

        AuthRefreshResponse response = client.refresh("ACCESS_TOKEN=expired; REFRESH_TOKEN=refresh");

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getSetCookieHeaders()).hasSize(2);
        assertThat(response.getSetCookieHeaders().get(0)).contains("ACCESS_TOKEN=new-access");
        assertThat(response.getSetCookieHeaders().get(1)).contains("REFRESH_TOKEN=new-refresh");
        server.verify();
    }

    @Test
    void authTimeoutIsMappedToGatewayAuthException() throws IOException {
        HttpServer server = slowAuthServer();
        try {
            GatewayProperties properties = gatewayProperties();
            properties.getRoutes().getServices().setAuthUrl(URI.create("http://localhost:" + server.getAddress().getPort()));
            properties.getAuth().setTimeout(Duration.ofMillis(100));
            properties.getAuth().getCache().setEnabled(false);

            AuthVerificationClient client = new AuthVerificationClient(
                    RestClient.builder(),
                    properties,
                    CircuitBreaker.ofDefaults("authVerification")
            );

            assertThatThrownBy(() -> client.verify("valid-token", TokenType.ACCESS))
                    .isInstanceOf(GatewayAuthException.class)
                    .hasMessage("auth verification service is unavailable.");
        } finally {
            server.stop(0);
        }
    }

    private GatewayProperties gatewayProperties() {
        GatewayProperties properties = new GatewayProperties();
        properties.getRoutes().getServices().setAuthUrl(URI.create("http://localhost:18081"));
        properties.getAuth().setVerifyPath("/api/v1/auth/tokens/verify");
        properties.getAuth().setRefreshPath("/api/auth/tokens/refresh");
        properties.getAuth().setTimeout(Duration.ofSeconds(3));
        properties.getAuth().getCache().setEnabled(true);
        properties.getAuth().getCache().setTtl(Duration.ofSeconds(5));
        properties.getAuth().getCache().setMaxSize(1000);
        properties.getInternal().setGatewayId("gateway-test-1");
        properties.getInternal().setGatewaySecret("gateway-secret-test");
        return properties;
    }

    private HttpServer slowAuthServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/api/v1/auth/tokens/verify", exchange -> {
            try {
                Thread.sleep(1000);
                byte[] body = "{}".getBytes();
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (IOException ignored) {
                // Client timeouts can close the socket before the test server writes the response.
            } finally {
                exchange.close();
            }
        });
        server.start();
        return server;
    }
}
