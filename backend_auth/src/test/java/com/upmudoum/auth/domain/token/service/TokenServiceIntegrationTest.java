package com.upmudoum.auth.domain.token.service;

import com.upmudoum.auth.domain.auth.dto.LoginGroupRequest;
import com.upmudoum.auth.domain.auth.dto.LoginRequest;
import com.upmudoum.auth.domain.auth.service.AuthService;
import com.upmudoum.auth.domain.auth.vo.LoginResult;
import com.upmudoum.auth.domain.token.dto.RefreshTokenRequest;
import com.upmudoum.auth.domain.token.dto.TokenRevokeRequest;
import com.upmudoum.auth.domain.token.dto.TokenVerifyRequest;
import com.upmudoum.auth.domain.token.dto.TokenVerifyResponse;
import com.upmudoum.auth.domain.token.repository.RefreshTokenRepository;
import com.upmudoum.auth.domain.token.vo.TokenRefreshResult;
import com.upmudoum.auth.domain.token.vo.TokenType;
import com.upmudoum.auth.exception.ApiException;
import com.upmudoum.auth.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TokenServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void verifyAccessTokenReturnsGatewayFriendlyPayload() {
        LoginResult loginResponse = authService.login(loginRequest(List.of("ERP")));

        TokenVerifyResponse response = tokenService.verify(
                new TokenVerifyRequest(loginResponse.getAccessToken(), TokenType.ACCESS)
        );

        assertThat(response.isValid()).isTrue();
        assertThat(response.getSubject()).isEqualTo("user-100");
        assertThat(response.getTokenType()).isEqualTo(TokenType.ACCESS);
        assertThat(response.getPermissions()).containsExactly("USER");
    }

    @Test
    void refreshRotatesRefreshTokenAndRejectsReuse() {
        LoginResult loginResponse = authService.login(loginRequest(List.of("ERP")));

        TokenRefreshResult refreshed = tokenService.refresh(new RefreshTokenRequest(loginResponse.getRefreshToken()));

        assertThat(refreshTokenRepository.existsByTokenIdAndRevokedAtIsNull(extractTokenId(refreshed.getRefreshToken())))
                .isTrue();

        assertThatThrownBy(() -> tokenService.refresh(new RefreshTokenRequest(loginResponse.getRefreshToken())))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.REFRESH_TOKEN_REUSE_DETECTED.message());
    }

    @Test
    void logoutRevokesRefreshToken() {
        LoginResult loginResponse = authService.login(loginRequest(List.of("ERP")));

        tokenService.logout(new TokenRevokeRequest(loginResponse.getRefreshToken()));

        assertThatThrownBy(() -> tokenService.verify(
                new TokenVerifyRequest(loginResponse.getRefreshToken(), TokenType.REFRESH)))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.TOKEN_REVOKED.message());
    }

    private String extractTokenId(String token) {
        return tokenService.verify(new TokenVerifyRequest(token, TokenType.REFRESH)).getTokenId();
    }

    private LoginRequest loginRequest(List<String> serviceAccesses) {
        return new LoginRequest(
                "COM001",
                "user-100",
                "demo-user",
                "ERP",
                serviceAccesses,
                List.of(new LoginGroupRequest("COM001", "ERP", "ADMIN", "ADMIN")),
                List.of("USER")
        );
    }
}
