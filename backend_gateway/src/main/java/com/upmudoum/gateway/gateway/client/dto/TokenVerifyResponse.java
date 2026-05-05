package com.upmudoum.gateway.gateway.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TokenVerifyResponse {

    private boolean valid;
    private TokenType tokenType;
    private String subject;
    private String audience;
    private String tokenId;
    private Instant issuedAt;
    private Instant expiresAt;
    private List<String> permissions = new ArrayList<>();
}
