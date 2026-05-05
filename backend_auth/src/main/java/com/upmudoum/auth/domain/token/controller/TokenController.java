package com.upmudoum.auth.domain.token.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.auth.common.api.ApiResponse;
import com.upmudoum.auth.common.web.AuthCookieManager;
import com.upmudoum.auth.domain.token.dto.RefreshTokenRequest;
import com.upmudoum.auth.domain.token.dto.RefreshTokenResponse;
import com.upmudoum.auth.domain.token.dto.TokenRevokeRequest;
import com.upmudoum.auth.domain.token.dto.TokenRevokeResponse;
import com.upmudoum.auth.domain.token.dto.TokenVerifyRequest;
import com.upmudoum.auth.domain.token.dto.TokenVerifyResponse;
import com.upmudoum.auth.domain.token.service.TokenService;
import com.upmudoum.auth.domain.token.vo.TokenRefreshResult;
import com.upmudoum.auth.exception.ApiException;
import com.upmudoum.auth.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/auth/tokens", "/api/auth/tokens"})
public class TokenController {

    private final TokenService tokenService;
    private final AuthCookieManager authCookieManager;
    private final ObjectMapper objectMapper;

    public TokenController(TokenService tokenService, AuthCookieManager authCookieManager, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.authCookieManager = authCookieManager;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/verify")
    public ApiResponse<TokenVerifyResponse> verify(@Valid @RequestBody TokenVerifyRequest request) {
        return ApiResponse.success(tokenService.verify(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        RefreshTokenRequest request = parseRefreshRequest(servletRequest);
        RefreshTokenRequest resolvedRequest = resolveRefreshRequest(request, servletRequest);
        TokenRefreshResult refreshResult = tokenService.refresh(resolvedRequest);
        authCookieManager.writeRefreshCookies(servletResponse, refreshResult);
        return ApiResponse.success(RefreshTokenResponse.builder()
                .subject(refreshResult.getSubject())
                .accessTokenExpiresAt(refreshResult.getAccessTokenExpiresAt())
                .refreshTokenExpiresAt(refreshResult.getRefreshTokenExpiresAt())
                .roles(refreshResult.getRoles())
                .tokenDeliveryMethod("COOKIE")
                .build());
    }

    private RefreshTokenRequest parseRefreshRequest(HttpServletRequest servletRequest) {
        try {
            byte[] body = servletRequest.getInputStream().readAllBytes();
            if (body.length == 0) {
                return null;
            }

            String content = new String(body, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                return null;
            }

            return objectMapper.readValue(content, RefreshTokenRequest.class);
        } catch (IOException exception) {
            throw new ApiException(ErrorCode.INVALID_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ApiResponse<TokenRevokeResponse> logout(
            @RequestBody(required = false) TokenRevokeRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        TokenRevokeResponse response = tokenService.logout(resolveRevokeRequest(request, servletRequest));
        authCookieManager.clearAuthCookies(servletResponse);
        return ApiResponse.success(response);
    }

    @PostMapping("/force-expire")
    public ApiResponse<TokenRevokeResponse> forceExpire(
            @RequestBody(required = false) TokenRevokeRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse
    ) {
        TokenRevokeResponse response = tokenService.forceExpire(resolveRevokeRequest(request, servletRequest));
        authCookieManager.clearAuthCookies(servletResponse);
        return ApiResponse.success(response);
    }

    private RefreshTokenRequest resolveRefreshRequest(RefreshTokenRequest request, HttpServletRequest servletRequest) {
        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            return request;
        }

        return authCookieManager.resolveRefreshToken(servletRequest)
                .map(RefreshTokenRequest::new)
                .orElseThrow(() -> new ApiException(ErrorCode.REFRESH_TOKEN_REQUIRED));
    }

    private TokenRevokeRequest resolveRevokeRequest(TokenRevokeRequest request, HttpServletRequest servletRequest) {
        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            return request;
        }

        return authCookieManager.resolveRefreshToken(servletRequest)
                .map(TokenRevokeRequest::new)
                .orElseThrow(() -> new ApiException(ErrorCode.REFRESH_TOKEN_REQUIRED));
    }
}
