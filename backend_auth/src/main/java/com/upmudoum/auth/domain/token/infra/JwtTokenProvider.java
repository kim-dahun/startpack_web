package com.upmudoum.auth.domain.token.infra;

import com.upmudoum.auth.config.security.AuthJwtProperties;
import com.upmudoum.auth.domain.token.vo.TokenIssueResult;
import com.upmudoum.auth.domain.token.vo.TokenType;
import com.upmudoum.auth.domain.token.vo.VerifiedToken;
import com.upmudoum.auth.exception.ApiException;
import com.upmudoum.auth.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final AuthJwtProperties properties;

    public JwtTokenProvider(AuthJwtProperties properties) {
        this.properties = properties;
    }

    public TokenIssueResult issueAccessToken(String subject, List<String> roles) {
        return issueToken(TokenType.ACCESS, subject, roles);
    }

    public TokenIssueResult issueRefreshToken(String subject, List<String> roles) {
        return issueToken(TokenType.REFRESH, subject, roles);
    }

    public TokenIssueResult issueOpenApiToken(String subject, List<String> scopes) {
        return issueToken(TokenType.OPEN_API, subject, scopes);
    }

    public VerifiedToken verifyToken(String token, TokenType expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .clockSkewSeconds(properties.getClockSkew().getSeconds())
                    .verifyWith(secretKey(secret(expectedType)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String actualType = claims.get("typ", String.class);
            if (!expectedType.name().equals(actualType)) {
                throw new ApiException(ErrorCode.TOKEN_TYPE_MISMATCH);
            }

            String permissionsKey = expectedType == TokenType.OPEN_API ? "scopes" : "roles";
            List<String> permissions = claims.get(permissionsKey, List.class);
            return new VerifiedToken(
                    expectedType,
                    claims.getSubject(),
                    claims.getAudience().stream().findFirst().orElse(null),
                    truncateToSeconds(claims.getIssuedAt()),
                    truncateToSeconds(claims.getExpiration()),
                    claims.getId(),
                    permissions == null ? List.of() : permissions
            );
        } catch (ExpiredJwtException exception) {
            throw new ApiException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

    private TokenIssueResult issueToken(TokenType tokenType, String subject, List<String> scopesOrRoles) {
        AuthJwtProperties.TokenSpec spec = switch (tokenType) {
            case ACCESS -> properties.getAccess();
            case REFRESH -> properties.getRefresh();
            case OPEN_API -> properties.getOpenApi();
        };

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(spec.getTtl());
        String tokenId = UUID.randomUUID().toString();
        String claimKey = tokenType == TokenType.OPEN_API ? "scopes" : "roles";

        String issuedToken = Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(subject)
                .audience().add(spec.getAudience()).and()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .id(tokenId)
                .claim("typ", tokenType.name())
                .claim(claimKey, scopesOrRoles)
                .signWith(secretKey(spec.getSecret()))
                .compact();

        return new TokenIssueResult(tokenType, issuedToken, subject, spec.getAudience(), issuedAt, expiresAt, scopesOrRoles, tokenId);
    }

    private String secret(TokenType tokenType) {
        return switch (tokenType) {
            case ACCESS -> properties.getAccess().getSecret();
            case REFRESH -> properties.getRefresh().getSecret();
            case OPEN_API -> properties.getOpenApi().getSecret();
        };
    }

    private SecretKey secretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Instant truncateToSeconds(Date date) {
        return date.toInstant().truncatedTo(ChronoUnit.SECONDS);
    }
}
