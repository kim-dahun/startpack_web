package com.upmudoum.auth.domain.token.repository;

import com.upmudoum.auth.domain.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenId(String tokenId);

    boolean existsByTokenIdAndRevokedAtIsNull(String tokenId);
}
