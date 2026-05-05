package com.upmudoum.gateway.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import com.upmudoum.gateway.exception.GatewayAuthException;
import com.upmudoum.gateway.gateway.client.AuthVerificationClient;
import com.upmudoum.gateway.gateway.client.dto.AuthRefreshResponse;
import com.upmudoum.gateway.gateway.client.dto.TokenType;
import com.upmudoum.gateway.gateway.client.dto.TokenVerifyResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class AuthenticationFilterUnitTests {

    @Test
    void verifiedAccessTokenAddsDownstreamHeaders() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        TokenVerifyResponse verifyResponse = verifyResponse(TokenType.ACCESS);
        when(authVerificationClient.verify("valid-token", TokenType.ACCESS)).thenReturn(verifyResponse);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.USER_ID)).isEqualTo("user-1");
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.COMPANY_CODE)).isEqualTo("COMPANY");
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.ROLES)).isEqualTo("ROLE_USER,ROLE_ADMIN");
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.TOKEN_TYPE)).isEqualTo("ACCESS");
    }

    @Test
    void accessTokenCookieIsUsedForAuthenticatedRoute() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        TokenVerifyResponse verifyResponse = verifyResponse(TokenType.ACCESS);
        when(authVerificationClient.verify("cookie-token", TokenType.ACCESS)).thenReturn(verifyResponse);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.setCookies(new jakarta.servlet.http.Cookie("ACCESS_TOKEN", "cookie-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.USER_ID)).isEqualTo("user-1");
    }

    @Test
    void expiredAccessCookieUsesRefreshCookieAndContinuesOriginalRequest() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.verify("expired-access-token", TokenType.ACCESS))
                .thenThrow(new GatewayAuthException("INVALID_TOKEN", "token verification failed."));
        when(authVerificationClient.refresh("ACCESS_TOKEN=expired-access-token; REFRESH_TOKEN=refresh-token"))
                .thenReturn(new AuthRefreshResponse("new-access-token", List.of(
                        "ACCESS_TOKEN=new-access-token; Path=/; HttpOnly; SameSite=Lax",
                        "REFRESH_TOKEN=new-refresh-token; Path=/; HttpOnly; SameSite=Lax"
                )));
        when(authVerificationClient.verify("new-access-token", TokenType.ACCESS))
                .thenReturn(verifyResponse(TokenType.ACCESS));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.COOKIE, "ACCESS_TOKEN=expired-access-token; REFRESH_TOKEN=refresh-token");
        request.setCookies(
                new jakarta.servlet.http.Cookie("ACCESS_TOKEN", "expired-access-token"),
                new jakarta.servlet.http.Cookie("REFRESH_TOKEN", "refresh-token")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).containsExactly(
                "ACCESS_TOKEN=new-access-token; Path=/; HttpOnly; SameSite=Lax",
                "REFRESH_TOKEN=new-refresh-token; Path=/; HttpOnly; SameSite=Lax"
        );
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.USER_ID)).isEqualTo("user-1");
        assertThat(downstreamRequest.get().getHeader(HttpHeaders.COOKIE))
                .isEqualTo("ACCESS_TOKEN=new-access-token; REFRESH_TOKEN=new-refresh-token");
        assertThat(downstreamRequest.get().getHeader(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer new-access-token");
    }

    @Test
    void missingAccessCookieUsesRefreshCookieAndContinuesOriginalRequest(CapturedOutput output) throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.refresh("REFRESH_TOKEN=refresh-token"))
                .thenReturn(new AuthRefreshResponse("new-access-token", List.of(
                        "ACCESS_TOKEN=new-access-token; Path=/; HttpOnly; SameSite=Lax",
                        "REFRESH_TOKEN=new-refresh-token; Path=/; HttpOnly; SameSite=Lax"
                )));
        when(authVerificationClient.verify("new-access-token", TokenType.ACCESS))
                .thenReturn(verifyResponse(TokenType.ACCESS));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.COOKIE, "REFRESH_TOKEN=refresh-token");
        request.setCookies(new jakarta.servlet.http.Cookie("REFRESH_TOKEN", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();
        AtomicInteger downstreamCallCount = new AtomicInteger();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
            downstreamCallCount.incrementAndGet();
            downstreamRequest.set((HttpServletRequest) servletRequest);
        });

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).containsExactly(
                "ACCESS_TOKEN=new-access-token; Path=/; HttpOnly; SameSite=Lax",
                "REFRESH_TOKEN=new-refresh-token; Path=/; HttpOnly; SameSite=Lax"
        );
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.USER_ID)).isEqualTo("user-1");
        assertThat(downstreamRequest.get().getHeader(HttpHeaders.COOKIE))
                .isEqualTo("REFRESH_TOKEN=new-refresh-token; ACCESS_TOKEN=new-access-token");
        assertThat(downstreamRequest.get().getHeader(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer new-access-token");
        assertThat(downstreamCallCount.get()).isEqualTo(1);
        assertThat(output)
                .contains("accessCookieMissing=true")
                .contains("refreshCookiePresent=true")
                .contains("refreshAttempt=true")
                .contains("refreshRecovered=true")
                .contains("setCookieCount=2");
    }

    @Test
    void missingAccessCookieWithoutRawCookieHeaderUsesServletCookiesAndContinuesOriginalRequest() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.refresh("REFRESH_TOKEN=refresh-token"))
                .thenReturn(new AuthRefreshResponse("new-access-token", List.of(
                        "ACCESS_TOKEN=new-access-token; Path=/; HttpOnly; SameSite=Lax",
                        "REFRESH_TOKEN=new-refresh-token; Path=/; HttpOnly; SameSite=Lax"
                )));
        when(authVerificationClient.verify("new-access-token", TokenType.ACCESS))
                .thenReturn(verifyResponse(TokenType.ACCESS));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.setCookies(new jakarta.servlet.http.Cookie("REFRESH_TOKEN", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).contains(
                "ACCESS_TOKEN=new-access-token; Path=/; HttpOnly; SameSite=Lax",
                "REFRESH_TOKEN=new-refresh-token; Path=/; HttpOnly; SameSite=Lax"
        );
        assertThat(downstreamRequest.get().getHeader(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer new-access-token");
        verify(authVerificationClient).refresh("REFRESH_TOKEN=refresh-token");
    }

    @Test
    void customRefreshCookieNameIsUsedForAutomaticRefresh() throws Exception {
        GatewayProperties properties = testProperties();
        properties.getAuth().setRefreshTokenCookieName("CUSTOM_REFRESH");
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.refresh("CUSTOM_REFRESH=refresh-token"))
                .thenReturn(new AuthRefreshResponse("new-access-token", List.of(
                        "ACCESS_TOKEN=new-access-token; Path=/; HttpOnly; SameSite=Lax",
                        "CUSTOM_REFRESH=new-refresh-token; Path=/; HttpOnly; SameSite=Lax"
                )));
        when(authVerificationClient.verify("new-access-token", TokenType.ACCESS))
                .thenReturn(verifyResponse(TokenType.ACCESS));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.COOKIE, "CUSTOM_REFRESH=refresh-token");
        request.setCookies(new jakarta.servlet.http.Cookie("CUSTOM_REFRESH", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(downstreamRequest.get().getHeader(HttpHeaders.COOKIE))
                .isEqualTo("CUSTOM_REFRESH=new-refresh-token; ACCESS_TOKEN=new-access-token");
    }

    @Test
    void missingAccessCookieReturnsServiceUnavailableWhenRefreshAuthIsUnavailable() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.refresh("REFRESH_TOKEN=refresh-token"))
                .thenThrow(new GatewayAuthException("AUTH_SERVER_UNAVAILABLE", "auth refresh service is unavailable."));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.COOKIE, "REFRESH_TOKEN=refresh-token");
        request.setCookies(new jakarta.servlet.http.Cookie("REFRESH_TOKEN", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getContentAsString()).contains("AUTH_SERVER_UNAVAILABLE");
    }

    @Test
    void missingAccessCookieReturnsUnauthorizedWhenRefreshFails() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.refresh("REFRESH_TOKEN=bad-refresh-token"))
                .thenThrow(new GatewayAuthException("INVALID_TOKEN", "token refresh failed."));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.COOKIE, "REFRESH_TOKEN=bad-refresh-token");
        request.setCookies(new jakarta.servlet.http.Cookie("REFRESH_TOKEN", "bad-refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("INVALID_TOKEN");
    }

    @Test
    void expiredAccessCookieReturnsUnauthorizedWhenRefreshFails() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.verify("expired-access-token", TokenType.ACCESS))
                .thenThrow(new GatewayAuthException("INVALID_TOKEN", "token verification failed."));
        when(authVerificationClient.refresh("ACCESS_TOKEN=expired-access-token; REFRESH_TOKEN=bad-refresh-token"))
                .thenThrow(new GatewayAuthException("INVALID_TOKEN", "token refresh failed."));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.COOKIE, "ACCESS_TOKEN=expired-access-token; REFRESH_TOKEN=bad-refresh-token");
        request.setCookies(
                new jakarta.servlet.http.Cookie("ACCESS_TOKEN", "expired-access-token"),
                new jakarta.servlet.http.Cookie("REFRESH_TOKEN", "bad-refresh-token")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("INVALID_TOKEN");
    }

    @Test
    void authorizationHeaderDoesNotUseAutomaticRefresh() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.verify("expired-bearer-token", TokenType.ACCESS))
                .thenThrow(new GatewayAuthException("INVALID_TOKEN", "token verification failed."));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer expired-bearer-token");
        request.addHeader(HttpHeaders.COOKIE, "REFRESH_TOKEN=refresh-token");
        request.setCookies(new jakarta.servlet.http.Cookie("REFRESH_TOKEN", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
        verify(authVerificationClient, never()).refresh("REFRESH_TOKEN=refresh-token");
    }

    @Test
    void authServerUnavailableReturnsServiceUnavailable() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.verify("valid-token", TokenType.ACCESS))
                .thenThrow(new GatewayAuthException("AUTH_SERVER_UNAVAILABLE", "auth verification service is unavailable."));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getContentAsString()).contains("AUTH_SERVER_UNAVAILABLE");
    }

    @Test
    void openCircuitReturnsServiceUnavailable() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        when(authVerificationClient.verify("valid-token", TokenType.ACCESS))
                .thenThrow(new GatewayAuthException("AUTH_CIRCUIT_OPEN", "auth verification circuit breaker is open."));
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getContentAsString()).contains("AUTH_CIRCUIT_OPEN");
    }

    @Test
    void configuredAuthorizationRuleReturnsForbiddenWhenRoleIsMissing() throws Exception {
        GatewayProperties properties = testProperties();
        GatewayProperties.Rule rule = new GatewayProperties.Rule();
        rule.setPathPattern("/api/admin/**");
        rule.getRequiredRoles().add("ROLE_ADMIN");
        properties.getAuthorization().getRules().add(rule);
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        TokenVerifyResponse verifyResponse = verifyResponse(TokenType.ACCESS);
        verifyResponse.setPermissions(List.of("ROLE_USER"));
        when(authVerificationClient.verify("valid-token", TokenType.ACCESS)).thenReturn(verifyResponse);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/users");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("INSUFFICIENT_PERMISSION");
    }

    @Test
    void configuredAuthorizationRulePassesWhenRoleExists() throws Exception {
        GatewayProperties properties = testProperties();
        GatewayProperties.Rule rule = new GatewayProperties.Rule();
        rule.setPathPattern("/api/admin/**");
        rule.getRequiredRoles().add("ROLE_ADMIN");
        properties.getAuthorization().getRules().add(rule);
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        TokenVerifyResponse verifyResponse = verifyResponse(TokenType.ACCESS);
        when(authVerificationClient.verify("valid-token", TokenType.ACCESS)).thenReturn(verifyResponse);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/users");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(downstreamRequest.get().getHeader(GatewayHeaders.ROLES)).isEqualTo("ROLE_USER,ROLE_ADMIN");
    }

    @Test
    void publicUserLoginRouteDoesNotRequireToken() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/users/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(downstreamRequest.get().getRequestURI()).isEqualTo("/api/users/login");
    }

    @Test
    void authLoginRouteIsNotPublicAnymore() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("TOKEN_REQUIRED");
    }

    @Test
    void internalTokenIssueRouteAllowsConfiguredGatewayCredentials() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.addHeader(GatewayHeaders.INTERNAL_GATEWAY_ID, "backend_gateway");
        request.addHeader(GatewayHeaders.INTERNAL_GATEWAY_SECRET, "local-dev-gateway-secret");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> downstreamRequest = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                downstreamRequest.set((HttpServletRequest) servletRequest));

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(downstreamRequest.get().getRequestURI()).isEqualTo("/api/auth/login");
    }

    @Test
    void internalTokenIssueRouteRejectsInvalidGatewayCredentials() throws Exception {
        GatewayProperties properties = testProperties();
        AuthVerificationClient authVerificationClient = mock(AuthVerificationClient.class);
        AuthenticationFilter filter = new AuthenticationFilter(properties, authVerificationClient, objectMapper());

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.addHeader(GatewayHeaders.INTERNAL_GATEWAY_ID, "wrong");
        request.addHeader(GatewayHeaders.INTERNAL_GATEWAY_SECRET, "wrong");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
        });

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("INTERNAL_GATEWAY_FORBIDDEN");
    }

    private GatewayProperties testProperties() {
        GatewayProperties properties = new GatewayProperties();
        properties.getRoutes().getPublicPaths().add("/actuator/health");
        properties.getRoutes().getPublicPaths().add("/api/users/login");
        properties.getRoutes().getPublicPaths().add("/api/users/signup");
        properties.getRoutes().getInternalPaths().add("/api/auth/login");
        properties.getRoutes().getOpenApiPaths().add("/open-api/**");
        properties.getInternal().setGatewayId("backend_gateway");
        properties.getInternal().setGatewaySecret("local-dev-gateway-secret");
        return properties;
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    private TokenVerifyResponse verifyResponse(TokenType tokenType) {
        TokenVerifyResponse response = new TokenVerifyResponse();
        response.setValid(true);
        response.setTokenType(tokenType);
        response.setSubject("user-1");
        response.setAudience("COMPANY");
        response.setPermissions(List.of("ROLE_USER", "ROLE_ADMIN"));
        return response;
    }
}
