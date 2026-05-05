package com.upmudoum.trade.domain.kis.service;

import com.upmudoum.trade.domain.kis.entity.KisOpenApiToken;
import com.upmudoum.trade.domain.kis.infra.AuthOpenApiTokenClient;
import com.upmudoum.trade.domain.kis.infra.KisOpenApiTokenClient;
import com.upmudoum.trade.domain.kis.repository.KisOpenApiTokenRepository;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KisOpenApiTokenService implements AuthOpenApiTokenClient {

    private static final Duration REISSUE_INTERVAL = Duration.ofHours(1);
    private static final Duration EXPIRY_SAFETY_MARGIN = Duration.ofMinutes(5);

    private final KisOpenApiTokenRepository repository;
    private final KisOpenApiTokenClient tokenClient;

    public KisOpenApiTokenService(KisOpenApiTokenRepository repository, KisOpenApiTokenClient tokenClient) {
        this.repository = repository;
        this.tokenClient = tokenClient;
    }

    @Override
    @Transactional(readOnly = true)
    public String issueOpenApiToken(KisTradeMode tradeMode) {
        return repository.findByTradeMode(tradeMode)
                .filter(this::isUsable)
                .map(KisOpenApiToken::getAccessToken)
                .orElseThrow(() -> new IllegalStateException("KIS open-api token is not issued yet. Wait for scheduler or call token refresh operation."));
    }

    @Transactional
    public void refreshIfNeeded(KisTradeMode tradeMode) {
        repository.findByTradeMode(tradeMode)
                .filter(this::shouldKeep)
                .ifPresentOrElse(token -> {
                }, () -> refresh(tradeMode));
    }

    @Transactional
    public KisOpenApiToken refresh(KisTradeMode tradeMode) {
        KisOpenApiTokenClient.IssuedToken issuedToken = tokenClient.issueOpenApiToken(tradeMode);
        Instant now = Instant.now();
        KisOpenApiToken token = repository.findByTradeMode(tradeMode).orElseGet(KisOpenApiToken::new);
        token.setTradeMode(tradeMode);
        token.setAccessToken(issuedToken.getAccessToken());
        token.setExpiresAt(issuedToken.getExpiresAt());
        token.setIssuedAt(now);
        token.setUpdatedAt(now);
        return repository.save(token);
    }

    private boolean shouldKeep(KisOpenApiToken token) {
        return token.getIssuedAt().plus(REISSUE_INTERVAL).isAfter(Instant.now()) && isUsable(token);
    }

    private boolean isUsable(KisOpenApiToken token) {
        return token.getExpiresAt().minus(EXPIRY_SAFETY_MARGIN).isAfter(Instant.now());
    }
}
