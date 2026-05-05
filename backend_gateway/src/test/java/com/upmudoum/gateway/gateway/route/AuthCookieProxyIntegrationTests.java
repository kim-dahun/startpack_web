package com.upmudoum.gateway.gateway.route;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthCookieProxyIntegrationTests {

    private static final AtomicReference<String> LAST_LOGIN_REQUEST_BODY = new AtomicReference<>();
    private static final AtomicReference<String> LAST_REFRESH_COOKIE = new AtomicReference<>();
    private static final AtomicReference<String> LAST_GATEWAY_ID = new AtomicReference<>();
    private static final AtomicReference<String> LAST_GATEWAY_SECRET = new AtomicReference<>();
    private static final HttpServer AUTH_SERVER = startAuthServer();

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void gatewayProperties(DynamicPropertyRegistry registry) {
        String authUrl = "http://localhost:" + AUTH_SERVER.getAddress().getPort();
        registry.add("gateway.routes.services.auth-url", () -> authUrl);
        registry.add("gateway.routes.services.user-url", () -> "http://localhost:18082");
        registry.add("gateway.routes.services.erp-url", () -> "http://localhost:18083");
        registry.add("gateway.routes.services.groupware-url", () -> "http://localhost:18084");
        registry.add("gateway.routes.services.trade-url", () -> "http://localhost:18085");
        registry.add("gateway.auth.verify-path", () -> "/api/v1/auth/tokens/verify");
        registry.add("gateway.auth.timeout", () -> "3s");
        registry.add("gateway.cors.allowed-origins", () -> "http://localhost:3000");
        registry.add("gateway.internal.gateway-id", () -> "gateway-integration-1");
        registry.add("gateway.internal.gateway-secret", () -> "integration-secret");
    }

    @AfterAll
    static void stopAuthServer() {
        AUTH_SERVER.stop(0);
    }

    @Test
    void internalTokenIssueRequestAndResponsePassThroughGatewayWithoutDamage() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(GatewayHeaders.INTERNAL_GATEWAY_ID, "gateway-integration-1");
        headers.add(GatewayHeaders.INTERNAL_GATEWAY_SECRET, "integration-secret");
        HttpEntity<String> request = new HttpEntity<>(
                "{\"comCd\":\"COM\",\"userId\":\"user-1\",\"serviceId\":\"ERP\",\"groups\":[\"ERP_USER\"]}",
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/login",
                HttpMethod.POST,
                request,
                String.class
        );

        List<String> setCookieHeaders = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(LAST_LOGIN_REQUEST_BODY.get()).contains("\"serviceId\":\"ERP\"");
        assertThat(LAST_GATEWAY_ID.get()).isEqualTo("gateway-integration-1");
        assertThat(LAST_GATEWAY_SECRET.get()).isEqualTo("integration-secret");
        assertThat(response.getBody()).contains("\"serviceId\":\"ERP\"");
        assertThat(response.getBody()).contains("\"serviceAccesses\":[\"ERP\",\"GROUPWARE\"]");
        assertThat(setCookieHeaders).hasSize(2);
        assertThat(setCookieHeaders.get(0)).contains("ACCESS_TOKEN=access-token", "HttpOnly", "SameSite=Lax", "Path=/");
        assertThat(setCookieHeaders.get(1)).contains("REFRESH_TOKEN=refresh-token", "HttpOnly", "SameSite=Lax", "Path=/");
    }

    @Test
    void refreshRequestForwardsAccessAndRefreshCookiesAndPreservesMultipleSetCookie(CapturedOutput output) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "ACCESS_TOKEN=expired-access-token; REFRESH_TOKEN=refresh-token");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/auth/tokens/refresh",
                HttpMethod.POST,
                request,
                String.class
        );

        List<String> setCookieHeaders = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(LAST_REFRESH_COOKIE.get())
                .contains("ACCESS_TOKEN=expired-access-token")
                .contains("REFRESH_TOKEN=refresh-token");
        assertThat(response.getBody()).contains("\"tokenDeliveryMethod\":\"COOKIE\"");
        assertThat(setCookieHeaders).hasSize(2);
        assertThat(setCookieHeaders.get(0)).contains("ACCESS_TOKEN=new-access-token", "HttpOnly", "SameSite=Lax", "Path=/");
        assertThat(setCookieHeaders.get(1)).contains("REFRESH_TOKEN=new-refresh-token", "HttpOnly", "SameSite=Lax", "Path=/");
        assertThat(output.getOut())
                .contains("path=/api/auth/tokens/refresh")
                .contains("downstream=auth:")
                .contains("status=200")
                .contains("cookieNames=[ACCESS_TOKEN, REFRESH_TOKEN]")
                .doesNotContain("expired-access-token")
                .doesNotContain("refresh-token");
    }

    private static HttpServer startAuthServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/api/auth/login", exchange -> {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                LAST_LOGIN_REQUEST_BODY.set(requestBody);
                LAST_GATEWAY_ID.set(exchange.getRequestHeaders().getFirst(GatewayHeaders.INTERNAL_GATEWAY_ID));
                LAST_GATEWAY_SECRET.set(exchange.getRequestHeaders().getFirst(GatewayHeaders.INTERNAL_GATEWAY_SECRET));
                byte[] body = """
                        {"success":true,"code":"OK","message":"success","data":{"serviceId":"ERP","serviceAccesses":["ERP","GROUPWARE"],"tokenDeliveryMethod":"COOKIE"}}
                        """
                        .getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                exchange.getResponseHeaders().add(HttpHeaders.SET_COOKIE,
                        "ACCESS_TOKEN=access-token; Max-Age=3600; Expires=Fri, 01 May 2026 04:00:00 GMT; Path=/; HttpOnly; SameSite=Lax");
                exchange.getResponseHeaders().add(HttpHeaders.SET_COOKIE,
                        "REFRESH_TOKEN=refresh-token; Max-Age=86400; Expires=Sat, 02 May 2026 03:00:00 GMT; Path=/; HttpOnly; SameSite=Lax");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            server.createContext("/api/auth/tokens/refresh", exchange -> {
                LAST_REFRESH_COOKIE.set(exchange.getRequestHeaders().getFirst(HttpHeaders.COOKIE));
                byte[] body = """
                        {"success":true,"code":"OK","message":"success","data":{"tokenDeliveryMethod":"COOKIE"}}
                        """
                        .getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                exchange.getResponseHeaders().add(HttpHeaders.SET_COOKIE,
                        "ACCESS_TOKEN=new-access-token; Max-Age=3600; Expires=Sun, 03 May 2026 04:00:00 GMT; Path=/; HttpOnly; SameSite=Lax");
                exchange.getResponseHeaders().add(HttpHeaders.SET_COOKIE,
                        "REFRESH_TOKEN=new-refresh-token; Max-Age=86400; Expires=Mon, 04 May 2026 03:00:00 GMT; Path=/; HttpOnly; SameSite=Lax");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            server.start();
            return server;
        } catch (IOException ex) {
            throw new IllegalStateException("failed to start test auth server", ex);
        }
    }
}
