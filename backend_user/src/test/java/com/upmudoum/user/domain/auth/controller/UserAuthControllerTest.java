package com.upmudoum.user.domain.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.auth.dto.LoginInitResponse;
import com.upmudoum.user.domain.auth.dto.LoginRequest;
import com.upmudoum.user.domain.auth.service.UserAuthService;
import com.upmudoum.user.domain.auth.vo.LoginResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

class UserAuthControllerTest {

    @Test
    void loginForwardsAllAuthSetCookieHeaders() {
        UserAuthService userAuthService = mock(UserAuthService.class);
        UserAuthController controller = new UserAuthController(userAuthService);
        LoginRequest request = new LoginRequest("COM001", "admin01", "secret", "ERP");
        LoginInitResponse loginResponse = new LoginInitResponse(null, "ERP", List.of("ERP"), null, List.of(), List.of(), List.of());
        String accessCookie = "ACCESS_TOKEN=access; Path=/; HttpOnly; SameSite=Lax";
        String refreshCookie = "REFRESH_TOKEN=refresh; Path=/; HttpOnly; SameSite=Lax";
        when(userAuthService.login(request)).thenReturn(new LoginResult(loginResponse, List.of(accessCookie, refreshCookie)));

        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ApiResponse<LoginInitResponse> response = controller.login(request, servletResponse);

        assertThat(response.getData()).isSameAs(loginResponse);
        assertThat(servletResponse.getHeaders(HttpHeaders.SET_COOKIE))
                .containsExactly(accessCookie, refreshCookie);
    }
}
