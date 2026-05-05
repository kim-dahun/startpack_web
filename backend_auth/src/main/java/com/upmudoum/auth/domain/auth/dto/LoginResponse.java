package com.upmudoum.auth.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private final String userId;
    private final String loginId;
    private final String serviceId;
    private final List<String> serviceAccesses;
    private final List<LoginGroupResponse> groups;
    private final Instant accessTokenExpiresAt;
    private final Instant refreshTokenExpiresAt;
    private final List<String> roles;
    private final String tokenDeliveryMethod;
}
