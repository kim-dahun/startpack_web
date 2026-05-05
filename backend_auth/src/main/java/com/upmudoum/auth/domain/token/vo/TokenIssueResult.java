package com.upmudoum.auth.domain.token.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TokenIssueResult {

    private final TokenType tokenType;
    private final String token;
    private final String subject;
    private final String audience;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final List<String> scopes;
    private final String tokenId;

}
