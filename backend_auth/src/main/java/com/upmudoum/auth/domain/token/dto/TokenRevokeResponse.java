package com.upmudoum.auth.domain.token.dto;

import com.upmudoum.auth.domain.token.vo.RevocationReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class TokenRevokeResponse {

    private final String subject;
    private final String tokenId;
    private final RevocationReason reason;
    private final Instant revokedAt;

}
