package com.upmudoum.auth.domain.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class LoginResult {

    private final String userId;
    private final String loginId;
    private final String serviceId;
    private final List<String> serviceAccesses;
    private final List<UserGroupSummary> groups;
    private final String accessToken;
    private final Instant accessTokenExpiresAt;
    private final String refreshToken;
    private final Instant refreshTokenExpiresAt;
    private final List<String> roles;
}
