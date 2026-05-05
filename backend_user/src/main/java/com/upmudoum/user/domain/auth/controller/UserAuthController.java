package com.upmudoum.user.domain.auth.controller;

import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.auth.dto.LoginInitResponse;
import com.upmudoum.user.domain.auth.dto.LoginRequest;
import com.upmudoum.user.domain.auth.dto.SignupRequest;
import com.upmudoum.user.domain.auth.dto.UserSummaryResponse;
import com.upmudoum.user.domain.auth.service.UserAuthService;
import com.upmudoum.user.domain.auth.vo.LoginResult;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/signup")
    public ApiResponse<UserSummaryResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.ok(userAuthService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginInitResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResult loginResult = userAuthService.login(request);
        loginResult.getSetCookieHeaders().forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie));
        return ApiResponse.ok(loginResult.getResponse());
    }
}
