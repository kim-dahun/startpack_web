package com.upmudoum.auth.domain.token.service;

import com.upmudoum.auth.domain.token.entity.RefreshToken;
import com.upmudoum.auth.domain.token.repository.RefreshTokenRepository;
import com.upmudoum.auth.domain.token.vo.RevocationReason;
import com.upmudoum.auth.domain.token.vo.TokenIssueResult;
import com.upmudoum.auth.exception.ApiException;
import com.upmudoum.auth.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class RefreshTokenStore {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenStore(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void save(TokenIssueResult refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .tokenId(refreshToken.getTokenId())
                .subject(refreshToken.getSubject())
                .tokenType(refreshToken.getTokenType())
                .expiresAt(refreshToken.getExpiresAt())
                .build());
    }

    @Transactional(readOnly = true)
    public RefreshToken requireActive(String tokenId) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new ApiException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshToken.isRevoked()) {
            if (RevocationReason.ROTATED.name().equals(refreshToken.getRevokeReason())) {
                throw new ApiException(ErrorCode.REFRESH_TOKEN_REUSE_DETECTED);
            }
            throw new ApiException(ErrorCode.TOKEN_REVOKED);
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(String tokenId, RevocationReason reason, String replacedByTokenId) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new ApiException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshToken.isRevoked()) {
            if (RevocationReason.ROTATED.name().equals(refreshToken.getRevokeReason())) {
                throw new ApiException(ErrorCode.REFRESH_TOKEN_REUSE_DETECTED);
            }
            throw new ApiException(ErrorCode.TOKEN_REVOKED);
        }

        refreshToken.revoke(Instant.now(), reason.name(), replacedByTokenId);
    }
}
