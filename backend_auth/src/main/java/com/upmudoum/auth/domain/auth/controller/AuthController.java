package com.upmudoum.auth.domain.auth.controller;

import com.upmudoum.auth.common.api.ApiResponse;
import com.upmudoum.auth.common.web.AuthCookieManager;
import com.upmudoum.auth.domain.auth.dto.LoginGroupResponse;
import com.upmudoum.auth.domain.auth.dto.LoginRequest;
import com.upmudoum.auth.domain.auth.dto.LoginResponse;
import com.upmudoum.auth.domain.auth.dto.OpenApiTokenRequest;
import com.upmudoum.auth.domain.auth.dto.OpenApiTokenResponse;
import com.upmudoum.auth.domain.auth.service.AuthService;
import com.upmudoum.auth.domain.auth.vo.LoginResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
public class AuthController {

    private final AuthService authService;
    private final AuthCookieManager authCookieManager;

    public AuthController(AuthService authService, AuthCookieManager authCookieManager) {
        this.authService = authService;
        this.authCookieManager = authCookieManager;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResult loginResult = authService.login(request);
        authCookieManager.writeLoginCookies(response, loginResult);
        return ApiResponse.success(LoginResponse.builder()
                .userId(loginResult.getUserId())
                .loginId(loginResult.getLoginId())
                .serviceId(loginResult.getServiceId())
                .serviceAccesses(loginResult.getServiceAccesses())
                .groups((loginResult.getGroups() == null ? List.<com.upmudoum.auth.domain.auth.vo.UserGroupSummary>of() : loginResult.getGroups()).stream()
                        .map(group -> LoginGroupResponse.builder()
                                .comCd(group.getComCd())
                                .serviceId(group.getServiceId())
                                .groupId(group.getGroupId())
                                .groupName(group.getGroupName())
                                .build())
                        .toList())
                .accessTokenExpiresAt(loginResult.getAccessTokenExpiresAt())
                .refreshTokenExpiresAt(loginResult.getRefreshTokenExpiresAt())
                .roles(loginResult.getRoles())
                .tokenDeliveryMethod("COOKIE")
                .build());
    }

    @PostMapping("/open-api/token")
    public ApiResponse<OpenApiTokenResponse> issueOpenApiToken(@Valid @RequestBody OpenApiTokenRequest request) {
        return ApiResponse.success(authService.issueOpenApiToken(request));
    }
}
