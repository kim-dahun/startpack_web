package com.upmudoum.auth.domain.token.service;

import com.upmudoum.auth.domain.audit.service.AuthAuditService;
import com.upmudoum.auth.domain.audit.vo.AuthAuditAction;
import com.upmudoum.auth.domain.audit.vo.AuthAuditOutcome;
import com.upmudoum.auth.domain.token.dto.RefreshTokenRequest;
import com.upmudoum.auth.domain.token.dto.RefreshTokenResponse;
import com.upmudoum.auth.domain.token.dto.TokenRevokeRequest;
import com.upmudoum.auth.domain.token.dto.TokenRevokeResponse;
import com.upmudoum.auth.domain.token.dto.TokenVerifyRequest;
import com.upmudoum.auth.domain.token.dto.TokenVerifyResponse;
import com.upmudoum.auth.domain.token.entity.RefreshToken;
import com.upmudoum.auth.domain.token.infra.JwtTokenProvider;
import com.upmudoum.auth.domain.token.vo.RevocationReason;
import com.upmudoum.auth.domain.token.vo.TokenIssueResult;
import com.upmudoum.auth.domain.token.vo.TokenRefreshResult;
import com.upmudoum.auth.domain.token.vo.TokenType;
import com.upmudoum.auth.domain.token.vo.VerifiedToken;
import com.upmudoum.auth.exception.ApiException;
import com.upmudoum.auth.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final AuthAuditService authAuditService;

    public TokenService(
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenStore refreshTokenStore,
            AuthAuditService authAuditService
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenStore = refreshTokenStore;
        this.authAuditService = authAuditService;
    }

    @Transactional(readOnly = true)
    public TokenVerifyResponse verify(TokenVerifyRequest request) {
        try {
            VerifiedToken verifiedToken = jwtTokenProvider.verifyToken(request.getToken(), request.getTokenType());
            if (request.getTokenType() == TokenType.REFRESH) {
                refreshTokenStore.requireActive(verifiedToken.getTokenId());
            }

            authAuditService.record(AuthAuditAction.TOKEN_VERIFY, AuthAuditOutcome.SUCCESS, verifiedToken.getSubject(), "OK", request.getTokenType().name());
            return TokenVerifyResponse.builder()
                    .valid(true)
                    .tokenType(verifiedToken.getTokenType())
                    .subject(verifiedToken.getSubject())
                    .audience(verifiedToken.getAudience())
                    .tokenId(verifiedToken.getTokenId())
                    .issuedAt(verifiedToken.getIssuedAt())
                    .expiresAt(verifiedToken.getExpiresAt())
                    .permissions(verifiedToken.getPermissions())
                    .build();
        } catch (ApiException exception) {
            authAuditService.record(AuthAuditAction.TOKEN_VERIFY, AuthAuditOutcome.FAILURE, null, exception.getErrorCode().code(), request.getTokenType().name());
            throw exception;
        }
    }

    @Transactional
    public TokenRefreshResult refresh(RefreshTokenRequest request) {
        try {
            VerifiedToken verifiedToken = jwtTokenProvider.verifyToken(request.getRefreshToken(), TokenType.REFRESH);
            RefreshToken currentRefreshToken = refreshTokenStore.requireActive(verifiedToken.getTokenId());

            TokenIssueResult newAccessToken = jwtTokenProvider.issueAccessToken(verifiedToken.getSubject(), verifiedToken.getPermissions());
            TokenIssueResult newRefreshToken = jwtTokenProvider.issueRefreshToken(verifiedToken.getSubject(), verifiedToken.getPermissions());

            refreshTokenStore.revoke(currentRefreshToken.getTokenId(), RevocationReason.ROTATED, newRefreshToken.getTokenId());
            refreshTokenStore.save(newRefreshToken);
            authAuditService.record(AuthAuditAction.TOKEN_REFRESH, AuthAuditOutcome.SUCCESS, verifiedToken.getSubject(), "OK", newRefreshToken.getTokenId());

            return TokenRefreshResult.builder()
                    .subject(verifiedToken.getSubject())
                    .accessToken(newAccessToken.getToken())
                    .accessTokenExpiresAt(newAccessToken.getExpiresAt())
                    .refreshToken(newRefreshToken.getToken())
                    .refreshTokenExpiresAt(newRefreshToken.getExpiresAt())
                    .roles(verifiedToken.getPermissions())
                    .build();
        } catch (ApiException exception) {
            authAuditService.record(AuthAuditAction.TOKEN_REFRESH, AuthAuditOutcome.FAILURE, null, exception.getErrorCode().code(), "refresh failed");
            throw exception;
        }
    }

    @Transactional
    public TokenRevokeResponse logout(TokenRevokeRequest request) {
        return revoke(request, RevocationReason.LOGOUT);
    }

    @Transactional
    public TokenRevokeResponse forceExpire(TokenRevokeRequest request) {
        return revoke(request, RevocationReason.FORCED_EXPIRE);
    }

    private TokenRevokeResponse revoke(TokenRevokeRequest request, RevocationReason reason) {
        try {
            VerifiedToken verifiedToken = jwtTokenProvider.verifyToken(request.getRefreshToken(), TokenType.REFRESH);
            if (verifiedToken.getTokenType() != TokenType.REFRESH) {
                throw new ApiException(ErrorCode.TOKEN_TYPE_MISMATCH);
            }

            java.time.Instant revokedAt = java.time.Instant.now();
            refreshTokenStore.revoke(verifiedToken.getTokenId(), reason, null);
            authAuditService.record(
                    reason == RevocationReason.LOGOUT ? AuthAuditAction.TOKEN_LOGOUT : AuthAuditAction.TOKEN_FORCE_EXPIRE,
                    AuthAuditOutcome.SUCCESS,
                    verifiedToken.getSubject(),
                    "OK",
                    reason.name()
            );
            return TokenRevokeResponse.builder()
                    .subject(verifiedToken.getSubject())
                    .tokenId(verifiedToken.getTokenId())
                    .reason(reason)
                    .revokedAt(revokedAt)
                    .build();
        } catch (ApiException exception) {
            authAuditService.record(
                    reason == RevocationReason.LOGOUT ? AuthAuditAction.TOKEN_LOGOUT : AuthAuditAction.TOKEN_FORCE_EXPIRE,
                    AuthAuditOutcome.FAILURE,
                    null,
                    exception.getErrorCode().code(),
                    reason.name()
            );
            throw exception;
        }
    }
}
