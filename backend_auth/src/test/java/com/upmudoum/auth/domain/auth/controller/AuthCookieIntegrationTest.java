package com.upmudoum.auth.domain.auth.controller;

import com.upmudoum.auth.config.security.AuthCookieProperties;
import com.upmudoum.auth.domain.auth.entity.OpenApiClient;
import com.upmudoum.auth.domain.auth.repository.OpenApiClientRepository;
import jakarta.servlet.http.Cookie;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
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
        "auth.cookie.secure=true",
        "auth.cookie.same-site=Lax",
        "auth.cookie.path=/",
        "auth.gateway.allowed-gateways[0].gateway-id=backend_gateway",
        "auth.gateway.allowed-gateways[0].shared-secret=test-gateway-secret"
})
class AuthCookieIntegrationTest {

    private static final String GATEWAY_ID_HEADER = "X-Internal-Gateway-Id";
    private static final String GATEWAY_SECRET_HEADER = "X-Internal-Gateway-Secret";
    private static final String GATEWAY_ID = "backend_gateway";
    private static final String GATEWAY_SECRET = "test-gateway-secret";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpenApiClientRepository openApiClientRepository;

    @Autowired
    private AuthCookieProperties authCookieProperties;

    @Test
    void loginIssuesHttpOnlyCookiesWithPolicy() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andExpect(jsonPath("$.data.groups[0].groupId").value("ADMIN"))
                .andExpect(jsonPath("$.data.accessToken").doesNotExist())
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andReturn();

        List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(2);
        assertCookie(setCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(setCookies, authCookieProperties.getRefreshTokenName(), 1209600);
    }

    @Test
    void refreshRotatesCookieUsingRefreshTokenCookie() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andReturn();

        List<String> setCookies = refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(2);
        assertCookie(setCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(setCookies, authCookieProperties.getRefreshTokenName(), 1209600);
        assertThat(extractCookieValue(setCookies, authCookieProperties.getAccessTokenName())).isNotEqualTo(
                extractCookieValue(loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE), authCookieProperties.getAccessTokenName())
        );
        assertThat(extractCookieValue(setCookies, authCookieProperties.getRefreshTokenName())).isNotEqualTo(refreshToken);
    }

    @Test
    void refreshAllowsGatewayCookieOnlyRequestWithoutBody() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andExpect(jsonPath("$.data.accessTokenExpiresAt").exists())
                .andExpect(jsonPath("$.data.refreshTokenExpiresAt").exists())
                .andReturn();

        List<String> setCookies = refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(2);
        assertCookie(setCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(setCookies, authCookieProperties.getRefreshTokenName(), 1209600);
    }

    @Test
    void refreshSucceedsWhenAccessCookieIsMissingButRefreshCookieExists() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andReturn();

        List<String> setCookies = refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(2);
        assertCookie(setCookies, authCookieProperties.getAccessTokenName(), 900);
        assertCookie(setCookies, authCookieProperties.getRefreshTokenName(), 1209600);
    }

    @Test
    void refreshSucceedsWithoutContentTypeWhenOnlyRefreshCookieExists() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE)).hasSize(2);
    }

    @Test
    void refreshSucceedsWithEmptyBodyWhenOnlyRefreshCookieExists() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokenDeliveryMethod").value("COOKIE"))
                .andReturn();

        assertThat(refreshResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE)).hasSize(2);
    }

    @Test
    void refreshRejectsRequestWhenRefreshCookieIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_012"))
                .andExpect(jsonPath("$.message").value("Refresh token required."));
    }

    @Test
    void refreshRejectsRevokedRefreshTokenFromGatewayCookie() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        mockMvc.perform(post("/api/auth/tokens/logout")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_006"))
                .andExpect(jsonPath("$.message").value("Token revoked."));
    }

    @Test
    void refreshRejectsReusedRefreshTokenFromGatewayCookie() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_008"))
                .andExpect(jsonPath("$.message").value("Refresh token reuse detected."));
    }

    @Test
    void refreshRejectsExpiredRefreshTokenFromGatewayCookie() throws Exception {
        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), expiredRefreshToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_005"))
                .andExpect(jsonPath("$.message").value("Token expired."));
    }

    @Test
    void refreshRejectsAccessTokenInRefreshCookie() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getAccessTokenName()
        );

        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), accessToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_004"))
                .andExpect(jsonPath("$.message").value("Invalid token."));
    }

    @Test
    void refreshRejectsRefreshTokenThatIsNotStored() throws Exception {
        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), validButUnstoredRefreshToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_009"))
                .andExpect(jsonPath("$.message").value("Refresh token not found."));
    }

    @Test
    void openApiTokenIssueReturnsBodyTokenWithoutAuthCookies() throws Exception {
        openApiClientRepository.deleteAll();
        openApiClientRepository.save(OpenApiClient.builder()
                .clientId("trade-client")
                .clientSecretHash(OpenApiClient.hashSecret("trade-secret"))
                .subject("system-trader")
                .scopesCsv("trade.read,trade.write")
                .enabled(true)
                .createdAt(Instant.now())
                .build());

        MvcResult result = mockMvc.perform(post("/api/auth/open-api/token")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": "trade-client",
                                  "clientSecret": "trade-secret",
                                  "subject": "system-trader"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.scopes[0]").value("trade.read"))
                .andReturn();

        assertThat(result.getResponse().getHeaders(HttpHeaders.SET_COOKIE)).isEmpty();
    }

    @Test
    void loginRejectsServiceWithoutAccessAndDoesNotIssueCookies() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson(List.of("TRADE"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_010"))
                .andReturn();

        assertThat(result.getResponse().getHeaders(HttpHeaders.SET_COOKIE)).isEmpty();
    }

    @Test
    void loginRejectsDirectRequestWithoutGatewayHeader() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_011"))
                .andExpect(jsonPath("$.message").value("Gateway access required."));
    }

    @Test
    void refreshRejectsDirectRequestWithoutGatewayHeader() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, GATEWAY_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = extractCookieValue(
                loginResult.getResponse().getHeaders(HttpHeaders.SET_COOKIE),
                authCookieProperties.getRefreshTokenName()
        );

        mockMvc.perform(post("/api/auth/tokens/refresh")
                        .cookie(new Cookie(authCookieProperties.getRefreshTokenName(), refreshToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_011"));
    }

    @Test
    void loginRejectsInvalidGatewaySecret() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .header(GATEWAY_ID_HEADER, GATEWAY_ID)
                        .header(GATEWAY_SECRET_HEADER, "wrong-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoginRequestJson()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_011"));
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
        return loginRequestJson(List.of("ERP"));
    }

    private String expiredRefreshToken() {
        Instant issuedAt = Instant.now().minusSeconds(120);
        Instant expiresAt = Instant.now().minusSeconds(60);
        return Jwts.builder()
                .issuer("backend-auth")
                .subject("user-100")
                .audience().add("backend-user").and()
                .issuedAt(java.util.Date.from(issuedAt))
                .expiration(java.util.Date.from(expiresAt))
                .id("expired-refresh-token")
                .claim("typ", "REFRESH")
                .claim("roles", List.of("USER"))
                .signWith(Keys.hmacShaKeyFor("change-me-refresh-secret-key-1234567890".getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private String validButUnstoredRefreshToken() {
        Instant issuedAt = Instant.now().minusSeconds(30);
        Instant expiresAt = Instant.now().plusSeconds(300);
        return Jwts.builder()
                .issuer("backend-auth")
                .subject("user-404")
                .audience().add("backend-user").and()
                .issuedAt(java.util.Date.from(issuedAt))
                .expiration(java.util.Date.from(expiresAt))
                .id("unstored-refresh-token")
                .claim("typ", "REFRESH")
                .claim("roles", List.of("USER"))
                .signWith(Keys.hmacShaKeyFor("change-me-refresh-secret-key-1234567890".getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private String loginRequestJson(List<String> serviceAccesses) {
        return """
                {
                  "comCd": "COM001",
                  "userId": "user-100",
                  "loginId": "demo-user",
                  "serviceId": "ERP",
                  "serviceAccesses": [%s],
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
                """.formatted(toJsonArray(serviceAccesses));
    }

    private String toJsonArray(List<String> values) {
        return values.stream()
                .map(value -> "\"" + value + "\"")
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }
}
