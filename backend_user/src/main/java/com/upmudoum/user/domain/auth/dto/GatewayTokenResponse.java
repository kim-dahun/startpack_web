package com.upmudoum.user.domain.auth.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GatewayTokenResponse {

    private String userId;
    private String loginId;
    private Instant accessTokenExpiresAt;
    private Instant refreshTokenExpiresAt;
    private List<String> roles;
    private String tokenDeliveryMethod;
}
