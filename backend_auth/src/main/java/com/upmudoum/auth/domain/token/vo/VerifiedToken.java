package com.upmudoum.auth.domain.token.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class VerifiedToken {

    private final TokenType tokenType;
    private final String subject;
    private final String audience;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final String tokenId;
    private final List<String> permissions;

}
