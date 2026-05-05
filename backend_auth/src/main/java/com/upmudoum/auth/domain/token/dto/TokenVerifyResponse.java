package com.upmudoum.auth.domain.token.dto;

import com.upmudoum.auth.domain.token.vo.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TokenVerifyResponse {

    private final boolean valid;
    private final TokenType tokenType;
    private final String subject;
    private final String audience;
    private final String tokenId;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final List<String> permissions;

}
