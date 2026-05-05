package com.upmudoum.auth.domain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RefreshTokenResponse {

    private final String subject;
    private final Instant accessTokenExpiresAt;
    private final Instant refreshTokenExpiresAt;
    private final List<String> roles;
    private final String tokenDeliveryMethod;
}
