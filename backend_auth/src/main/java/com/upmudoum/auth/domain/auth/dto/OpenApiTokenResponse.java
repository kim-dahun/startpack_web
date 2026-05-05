package com.upmudoum.auth.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OpenApiTokenResponse {

    private final String subject;
    private final String clientId;
    private final String accessToken;
    private final Instant accessTokenExpiresAt;
    private final List<String> scopes;

}
