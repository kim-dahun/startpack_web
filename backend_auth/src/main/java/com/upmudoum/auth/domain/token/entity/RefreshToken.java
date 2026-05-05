package com.upmudoum.auth.domain.token.entity;

import com.upmudoum.auth.domain.token.vo.TokenType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenId;

    @Column(nullable = false, length = 64)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TokenType tokenType;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    @Column(length = 30)
    private String revokeReason;

    @Column(length = 64)
    private String replacedByTokenId;

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke(Instant revokedAt, String revokeReason, String replacedByTokenId) {
        this.revokedAt = revokedAt;
        this.revokeReason = revokeReason;
        this.replacedByTokenId = replacedByTokenId;
    }
}
