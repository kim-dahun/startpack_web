package com.upmudoum.gateway.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.common.dto.GatewayErrorResponse;
import com.upmudoum.gateway.common.web.MutableHeaderHttpServletRequest;
import com.upmudoum.gateway.config.GatewayProperties;
import com.upmudoum.gateway.exception.GatewayAuthException;
import com.upmudoum.gateway.gateway.client.AuthVerificationClient;
import com.upmudoum.gateway.gateway.client.dto.AuthRefreshResponse;
import com.upmudoum.gateway.gateway.client.dto.TokenType;
import com.upmudoum.gateway.gateway.client.dto.TokenVerifyResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final GatewayProperties properties;
    private final AuthVerificationClient authVerificationClient;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthenticationFilter(
            GatewayProperties properties,
            AuthVerificationClient authVerificationClient,
            ObjectMapper objectMapper
    ) {
        this.properties = properties;
        this.authVerificationClient = authVerificationClient;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        if (CorsUtils.isPreFlightRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (matchesAny(requestPath, properties.getRoutes().getPublicPaths())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (matchesAny(requestPath, properties.getRoutes().getInternalPaths())) {
            if (!isAllowedInternalRequest(request)) {
                writeError(response, request.getHeader(GatewayHeaders.REQUEST_ID), HttpStatus.FORBIDDEN,
                        "INTERNAL_GATEWAY_FORBIDDEN", "internal gateway credentials are invalid.");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        TokenCredentials credentials = resolveToken(request, requestPath);
        if (!StringUtils.hasText(credentials.getToken())) {
            if (shouldAttemptRefreshWithoutAccessToken(credentials, request)) {
                try {
                    refreshAndContinue(request, response, filterChain, requestPath, "ACCESS_TOKEN_MISSING");
                    return;
                } catch (GatewayAuthException refreshEx) {
                    logRefreshFailure(request, "ACCESS_TOKEN_MISSING", refreshEx);
                    writeAuthError(response, request.getHeader(GatewayHeaders.REQUEST_ID), refreshEx);
                    return;
                }
            }
            writeError(response, request.getHeader(GatewayHeaders.REQUEST_ID), HttpStatus.UNAUTHORIZED,
                    "TOKEN_REQUIRED", "token is required.");
            return;
        }

        try {
            TokenVerifyResponse verifiedToken = authVerificationClient.verify(credentials.getToken(), credentials.getTokenType());
            continueWithVerifiedToken(request, response, filterChain, requestPath, verifiedToken);
        } catch (GatewayAuthException ex) {
            if (shouldAttemptRefresh(credentials, request, ex)) {
                try {
                    refreshAndContinue(request, response, filterChain, requestPath, "ACCESS_TOKEN_INVALID");
                    return;
                } catch (GatewayAuthException refreshEx) {
                    logRefreshFailure(request, "ACCESS_TOKEN_INVALID", refreshEx);
                    writeAuthError(response, request.getHeader(GatewayHeaders.REQUEST_ID), refreshEx);
                    return;
                }
            }
            writeAuthError(response, request.getHeader(GatewayHeaders.REQUEST_ID), ex);
        }
    }

    private void refreshAndContinue(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            String requestPath,
            String reason
    ) throws IOException, ServletException {
        String requestId = request.getHeader(GatewayHeaders.REQUEST_ID);
        String cookieHeader = resolveCookieHeader(request);
        log.info("gateway auth refresh attempt reason={} accessCookieMissing={} refreshCookiePresent={} refreshAttempt=true requestId={}",
                reason, !hasAccessTokenCookie(request), hasRefreshTokenCookie(request), requestId);
        AuthRefreshResponse refreshedToken = authVerificationClient.refresh(cookieHeader);
        refreshedToken.getSetCookieHeaders().forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie));
        TokenVerifyResponse verifiedToken = authVerificationClient.verify(refreshedToken.getAccessToken(), TokenType.ACCESS);
        log.info("gateway auth refresh success reason={} refreshRecovered=true setCookieCount={} requestId={}",
                reason, refreshedToken.getSetCookieHeaders().size(), requestId);
        continueWithVerifiedToken(request, response, filterChain, requestPath, verifiedToken,
                buildRefreshedCookieHeader(request, refreshedToken), refreshedToken.getAccessToken());
    }

    private void logRefreshFailure(HttpServletRequest request, String reason, GatewayAuthException ex) {
        log.warn("gateway auth refresh failure reason={} accessCookieMissing={} refreshCookiePresent={} refreshAttempt=true refreshRecovered=false code={} requestId={}",
                reason, !hasAccessTokenCookie(request), hasRefreshTokenCookie(request), ex.getCode(),
                request.getHeader(GatewayHeaders.REQUEST_ID));
    }

    private TokenCredentials resolveToken(HttpServletRequest request, String requestPath) {
        TokenType tokenType = resolveTokenType(requestPath);
        if (TokenType.OPEN_API.equals(tokenType)) {
            String openApiToken = request.getHeader(GatewayHeaders.OPEN_API_TOKEN);
            if (StringUtils.hasText(openApiToken)) {
                return new TokenCredentials(openApiToken, TokenType.OPEN_API, TokenSource.OPEN_API_HEADER);
            }
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return new TokenCredentials(authorizationHeader.substring(BEARER_PREFIX.length()).trim(),
                    tokenType, TokenSource.AUTHORIZATION_HEADER);
        }
        if (TokenType.ACCESS.equals(tokenType)) {
            String accessTokenCookie = resolveAccessTokenCookie(request);
            if (StringUtils.hasText(accessTokenCookie)) {
                return new TokenCredentials(accessTokenCookie, TokenType.ACCESS, TokenSource.ACCESS_COOKIE);
            }
        }
        return new TokenCredentials(null, tokenType, TokenSource.NONE);
    }

    private TokenType resolveTokenType(String requestPath) {
        if (matchesAny(requestPath, properties.getRoutes().getOpenApiPaths())) {
            return TokenType.OPEN_API;
        }
        return TokenType.ACCESS;
    }

    private String resolveAccessTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> properties.getAuth().getAccessTokenCookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private MutableHeaderHttpServletRequest withAuthenticatedHeaders(
            HttpServletRequest request,
            TokenVerifyResponse verifiedToken
    ) {
        MutableHeaderHttpServletRequest wrappedRequest = new MutableHeaderHttpServletRequest(request);
        wrappedRequest.putHeader(GatewayHeaders.USER_ID, verifiedToken.getSubject());
        wrappedRequest.putHeader(GatewayHeaders.COMPANY_CODE, verifiedToken.getAudience());
        wrappedRequest.putHeader(GatewayHeaders.ROLES, String.join(",", verifiedToken.getPermissions()));
        wrappedRequest.putHeader(GatewayHeaders.TOKEN_TYPE, verifiedToken.getTokenType().name());
        return wrappedRequest;
    }

    private boolean matchesAny(String requestPath, List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private boolean isAllowedInternalRequest(HttpServletRequest request) {
        GatewayProperties.Internal internal = properties.getInternal();
        String gatewayId = request.getHeader(GatewayHeaders.INTERNAL_GATEWAY_ID);
        String gatewaySecret = request.getHeader(GatewayHeaders.INTERNAL_GATEWAY_SECRET);
        return internal.getGatewayId().equals(gatewayId) && internal.getGatewaySecret().equals(gatewaySecret);
    }

    private boolean hasRequiredRole(String requestPath, TokenVerifyResponse verifiedToken) {
        return properties.getAuthorization().getRules().stream()
                .filter(rule -> pathMatcher.match(rule.getPathPattern(), requestPath))
                .findFirst()
                .map(rule -> verifiedToken.getPermissions().containsAll(rule.getRequiredRoles()))
                .orElse(true);
    }

    private void continueWithVerifiedToken(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            String requestPath,
            TokenVerifyResponse verifiedToken
    ) throws IOException, ServletException {
        if (!hasRequiredRole(requestPath, verifiedToken)) {
            writeError(response, request.getHeader(GatewayHeaders.REQUEST_ID), HttpStatus.FORBIDDEN,
                    "INSUFFICIENT_PERMISSION", "required permission is missing.");
            return;
        }
        filterChain.doFilter(withAuthenticatedHeaders(request, verifiedToken), response);
    }

    private void continueWithVerifiedToken(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            String requestPath,
            TokenVerifyResponse verifiedToken,
            String refreshedCookieHeader,
            String refreshedAccessToken
    ) throws IOException, ServletException {
        if (!hasRequiredRole(requestPath, verifiedToken)) {
            writeError(response, request.getHeader(GatewayHeaders.REQUEST_ID), HttpStatus.FORBIDDEN,
                    "INSUFFICIENT_PERMISSION", "required permission is missing.");
            return;
        }
        filterChain.doFilter(withAuthenticatedHeaders(request, verifiedToken, refreshedCookieHeader, refreshedAccessToken), response);
    }

    private boolean shouldAttemptRefresh(TokenCredentials credentials, HttpServletRequest request, GatewayAuthException ex) {
        return TokenSource.ACCESS_COOKIE.equals(credentials.getSource())
                && TokenType.ACCESS.equals(credentials.getTokenType())
                && "INVALID_TOKEN".equals(ex.getCode())
                && StringUtils.hasText(resolveCookieHeader(request))
                && hasRefreshTokenCookie(request);
    }

    private boolean shouldAttemptRefreshWithoutAccessToken(TokenCredentials credentials, HttpServletRequest request) {
        return TokenSource.NONE.equals(credentials.getSource())
                && TokenType.ACCESS.equals(credentials.getTokenType())
                && !StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION))
                && StringUtils.hasText(resolveCookieHeader(request))
                && hasRefreshTokenCookie(request);
    }

    private boolean hasAccessTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        return Arrays.stream(cookies)
                .anyMatch(cookie -> properties.getAuth().getAccessTokenCookieName().equals(cookie.getName())
                        && StringUtils.hasText(cookie.getValue()));
    }

    private boolean hasRefreshTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }
        return Arrays.stream(cookies)
                .anyMatch(cookie -> properties.getAuth().getRefreshTokenCookieName().equals(cookie.getName())
                        && StringUtils.hasText(cookie.getValue()));
    }

    private MutableHeaderHttpServletRequest withAuthenticatedHeaders(
            HttpServletRequest request,
            TokenVerifyResponse verifiedToken,
            String refreshedCookieHeader,
            String refreshedAccessToken
    ) {
        MutableHeaderHttpServletRequest wrappedRequest = withAuthenticatedHeaders(request, verifiedToken);
        wrappedRequest.putHeader(HttpHeaders.COOKIE, refreshedCookieHeader);
        wrappedRequest.putHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + refreshedAccessToken);
        return wrappedRequest;
    }

    private String buildRefreshedCookieHeader(HttpServletRequest request, AuthRefreshResponse refreshedToken) {
        Map<String, String> cookies = new LinkedHashMap<>();
        Cookie[] requestCookies = request.getCookies();
        if (requestCookies != null) {
            Arrays.stream(requestCookies)
                    .filter(cookie -> StringUtils.hasText(cookie.getName()) && StringUtils.hasText(cookie.getValue()))
                    .forEach(cookie -> cookies.put(cookie.getName(), cookie.getValue()));
        }
        refreshedToken.getSetCookieHeaders().forEach(setCookie -> {
            CookiePair cookie = parseSetCookie(setCookie);
            if (cookie != null) {
                cookies.put(cookie.name(), cookie.value());
            }
        });
        List<String> cookieValues = new ArrayList<>();
        cookies.forEach((name, value) -> cookieValues.add(name + "=" + value));
        return String.join("; ", cookieValues);
    }

    private String resolveCookieHeader(HttpServletRequest request) {
        String cookieHeader = request.getHeader(HttpHeaders.COOKIE);
        if (StringUtils.hasText(cookieHeader)) {
            return cookieHeader;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        List<String> cookieValues = Arrays.stream(cookies)
                .filter(cookie -> StringUtils.hasText(cookie.getName()) && StringUtils.hasText(cookie.getValue()))
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .toList();
        return String.join("; ", cookieValues);
    }

    private CookiePair parseSetCookie(String setCookieHeader) {
        if (!StringUtils.hasText(setCookieHeader)) {
            return null;
        }
        String cookie = setCookieHeader.split(";", 2)[0];
        int separator = cookie.indexOf('=');
        if (separator <= 0 || separator == cookie.length() - 1) {
            return null;
        }
        return new CookiePair(cookie.substring(0, separator), cookie.substring(separator + 1));
    }

    private void writeAuthError(HttpServletResponse response, String requestId, GatewayAuthException ex) throws IOException {
        HttpStatus status = ("AUTH_SERVER_UNAVAILABLE".equals(ex.getCode()) || "AUTH_CIRCUIT_OPEN".equals(ex.getCode()))
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.UNAUTHORIZED;
        writeError(response, requestId, status, ex.getCode(), ex.getMessage());
    }

    private void writeError(
            HttpServletResponse response,
            String requestId,
            HttpStatus status,
            String code,
            String message
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        log.warn("gateway auth failure code={} status={} requestId={}", code, status.value(), requestId);
        objectMapper.writeValue(response.getWriter(), new GatewayErrorResponse(code, message, requestId, Instant.now()));
    }

    private enum TokenSource {
        AUTHORIZATION_HEADER,
        ACCESS_COOKIE,
        OPEN_API_HEADER,
        NONE
    }

    private static class TokenCredentials {

        private final String token;
        private final TokenType tokenType;
        private final TokenSource source;

        TokenCredentials(String token, TokenType tokenType, TokenSource source) {
            this.token = token;
            this.tokenType = tokenType;
            this.source = source;
        }

        String getToken() {
            return token;
        }

        TokenType getTokenType() {
            return tokenType;
        }

        TokenSource getSource() {
            return source;
        }
    }

    private static class CookiePair {

        private final String name;
        private final String value;

        CookiePair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        String name() {
            return name;
        }

        String value() {
            return value;
        }
    }
}
