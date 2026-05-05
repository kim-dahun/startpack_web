package com.upmudoum.auth.common.web;

import com.upmudoum.auth.config.security.AuthCookieProperties;
import com.upmudoum.auth.config.security.AuthJwtProperties;
import com.upmudoum.auth.domain.auth.vo.LoginResult;
import com.upmudoum.auth.domain.token.vo.TokenRefreshResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AuthCookieManager {

    private final AuthCookieProperties authCookieProperties;
    private final AuthJwtProperties authJwtProperties;

    public AuthCookieManager(AuthCookieProperties authCookieProperties, AuthJwtProperties authJwtProperties) {
        this.authCookieProperties = authCookieProperties;
        this.authJwtProperties = authJwtProperties;
    }

    public void writeLoginCookies(HttpServletResponse response, LoginResult loginResult) {
        writeAuthCookies(
                response,
                loginResult.getAccessToken(),
                loginResult.getRefreshToken()
        );
    }

    public void writeRefreshCookies(HttpServletResponse response, TokenRefreshResult refreshResult) {
        writeAuthCookies(
                response,
                refreshResult.getAccessToken(),
                refreshResult.getRefreshToken()
        );
    }

    public void clearAuthCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(authCookieProperties.getAccessTokenName(), "", Duration.ZERO));
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(authCookieProperties.getRefreshTokenName(), "", Duration.ZERO));
    }

    public Optional<String> resolveRefreshToken(HttpServletRequest request) {
        return resolveCookie(request, authCookieProperties.getRefreshTokenName());
    }

    public String getAccessTokenName() {
        return authCookieProperties.getAccessTokenName();
    }

    public String getRefreshTokenName() {
        return authCookieProperties.getRefreshTokenName();
    }

    private void writeAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                authCookieProperties.getAccessTokenName(),
                accessToken,
                authJwtProperties.getAccess().getTtl()
        ));
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(
                authCookieProperties.getRefreshTokenName(),
                refreshToken,
                authJwtProperties.getRefresh().getTtl()
        ));
    }

    private Optional<String> resolveCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    private String buildCookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(authCookieProperties.isSecure())
                .sameSite(authCookieProperties.getSameSite())
                .path(authCookieProperties.getPath())
                .maxAge(maxAge)
                .build()
                .toString();
    }
}
