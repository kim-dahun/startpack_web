package com.upmudoum.auth.domain.auth.controller;

import com.upmudoum.auth.config.security.AuthCookieProperties;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "auth.cookie.access-token-name=AUTH_ACCESS",
        "auth.cookie.refresh-token-name=AUTH_REFRESH",
        "auth.cookie.secure=true",
        "auth.cookie.same-site=Lax",
        "auth.cookie.path=/",
        "auth.gateway.allowed-gateways[0].gateway-id=backend_gateway",
        "auth.gateway.allowed-gateways[0].shared-secret=test-gateway-secret"
})
class AuthCustomCookieIntegrationTest {

    private static final String GATEWAY_ID_HEADER = "X-Internal-Gateway-Id";
    private static final String GATEWAY_SECRET_HEADER = "X-Internal-Gateway-Secret";
    private static final String GATEWAY_ID = "backend_gateway";
    private static final String GATEWAY_SECRET = "test-gateway-secret";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthCookieProperties authCookieProperties;

    @Test
    void refreshReissuesBothCookiesUsingConfiguredCookieNames() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        List<String> loginCookies = loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        String refreshToken = extractCookieValue(loginCookies, authCookieProperties.getRefreshTokenName());

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andReturn();

        List<String> refreshCookies = refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(refreshCookies).hasSize(2);
        assertCookie(refreshCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(refreshCookies, authCookieProperties.getRefreshTokenName(), 1209600);
        assertThat(extractCookieValue(refreshCookies, authCookieProperties.getAccessTokenName())).isNotEqualTo(
                extractCookieValue(loginCookies, authCookieProperties.getAccessTokenName())
        );
        assertThat(extractCookieValue(refreshCookies, authCookieProperties.getRefreshTokenName())).isNotEqualTo(refreshToken);
    }

    @Test
    void refreshSucceedsWithOnlyCustomRefreshCookieAndNoAccessCookie() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        List<String> loginCookies = loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        String refreshToken = extractCookieValue(loginCookies, authCookieProperties.getRefreshTokenName());

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andReturn();

        List<String> refreshCookies = refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(refreshCookies).hasSize(2);
        assertCookie(refreshCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(refreshCookies, authCookieProperties.getRefreshTokenName(), 1209600);
    }

    @Test
    void refreshSucceedsWithoutContentTypeAndOnlyCustomRefreshCookie() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        List<String> loginCookies = loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        String refreshToken = extractCookieValue(loginCookies, authCookieProperties.getRefreshTokenName());

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andReturn();

        List<String> refreshCookies = refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(refreshCookies).hasSize(2);
        assertCookie(refreshCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(refreshCookies, authCookieProperties.getRefreshTokenName(), 1209600);
    }

    @Test
    void refreshSucceedsWithEmptyBodyAndOnlyCustomRefreshCookie() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        List<String> loginCookies = loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        String refreshToken = extractCookieValue(loginCookies, authCookieProperties.getRefreshTokenName());

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andReturn();

        List<String> refreshCookies = refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(refreshCookies).hasSize(2);
        assertCookie(refreshCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(refreshCookies, authCookieProperties.getRefreshTokenName(), 1209600);
    }

    private void assertCookie(List<String> cookies, String cookieName, long maxAge) {
        String cookie = cookies.stream()
                .filter(value -> value.startsWith(cookieName + "="))
                .findFirst()
                .orElseThrow();

        assertThat(cookie).contains("HttpOnly");
        assertThat(cookie).contains("Secure");
        assertThat(cookie).contains("SameSite=Lax");
        assertThat(cookie).contains("Path=/");
        assertThat(cookie).contains("Max-Age=" + maxAge);
    }

    private String extractCookieValue(List<String> cookies, String cookieName) {
        String cookie = cookies.stream()
                .filter(value -> value.startsWith(cookieName + "="))
                .findFirst()
                .orElseThrow();

        return cookie.substring((cookieName + "=").length(), cookie.indexOf(';'));
    }

    private String validLoginRequestJson() {
        return """
                {
                  "comCd": "COM001",
                  "userId": "user-100",
                  "loginId": "demo-user",
                  "serviceId": "ERP",
                  "serviceAccesses": ["ERP"],
                  "groups": [
                    {
                      "comCd": "COM001",
                      "serviceId": "ERP",
                      "groupId": "ADMIN",
                      "groupName": "ADMIN"
                    }
                  ],
                  "roles": ["USER"]
                }
                """;
    }
}
