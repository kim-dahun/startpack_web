package com.upmudoum.trade.domain.master.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "trade.master.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class TradeMasterImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(TradeMasterImportScheduler.class);

    private final TradeMasterImportService importService;

    public TradeMasterImportScheduler(TradeMasterImportService importService) {
        this.importService = importService;
    }

    @Scheduled(cron = "${trade.master.scheduler.cron:0 0 7 * * MON-FRI}", zone = "${trade.master.scheduler.zone:Asia/Seoul}")
    public void refreshDefaultMasters() {
        try {
            log.info("TRADE_MASTER_REFRESH_START");
            importService.downloadAndImportDefaults();
            log.info("TRADE_MASTER_REFRESH_SUCCESS");
        } catch (RuntimeException ex) {
            log.warn("TRADE_MASTER_REFRESH_FAIL reason={}", ex.getMessage(), ex);
        }
    }
}
