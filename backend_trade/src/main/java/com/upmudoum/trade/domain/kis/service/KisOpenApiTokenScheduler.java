package com.upmudoum.trade.domain.kis.service;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KisOpenApiTokenScheduler {

    private static final Logger log = LoggerFactory.getLogger(KisOpenApiTokenScheduler.class);

    private final KisOpenApiTokenService tokenService;
    private final boolean enabled;

    public KisOpenApiTokenScheduler(
            KisOpenApiTokenService tokenService,
            @Value("${trade.kis.token-scheduler.enabled:true}") boolean enabled
    ) {
        this.tokenService = tokenService;
        this.enabled = enabled;
    }

    @Scheduled(initialDelayString = "${trade.kis.token-scheduler.initial-delay-ms:3000}", fixedDelayString = "${trade.kis.token-scheduler.fixed-delay-ms:3600000}")
    public void refreshTokens() {
        if (!enabled) {
            return;
        }
        for (KisTradeMode tradeMode : KisTradeMode.values()) {
            try {
                tokenService.refreshIfNeeded(tradeMode);
            } catch (RuntimeException ex) {
                log.warn("KIS_TOKEN_REFRESH_FAIL tradeMode={} message={}", tradeMode, ex.getMessage());
            }
        }
    }
}
