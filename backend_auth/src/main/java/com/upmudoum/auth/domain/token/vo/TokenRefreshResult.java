package com.upmudoum.auth.domain.token.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TokenRefreshResult {

    private final String subject;
    private final String accessToken;
    private final Instant accessTokenExpiresAt;
    private final String refreshToken;
    private final Instant refreshTokenExpiresAt;
    private final List<String> roles;
}
