package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.kis.service.KisApiCallLogService;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "trade.kis.rest-client.enabled", havingValue = "false")
public class NoopKisRestClient implements KisRestClient {

    private static final Logger log = LoggerFactory.getLogger(NoopKisRestClient.class);
    private final KisApiCallLogService logService;

    public NoopKisRestClient(KisApiCallLogService logService) {
        this.logService = logService;
    }

    @Override
    public Map<String, Object> get(String endpoint, Map<String, String> query, KisTradeMode tradeMode) {
        long startedAt = System.currentTimeMillis();
        log.info("KIS_CALL_START mock=true method=GET endpoint={} tradeMode={} query={}", endpoint, tradeMode, query);
        long duration = System.currentTimeMillis() - startedAt;
        logService.log("GET", endpoint, 200, duration, null, null);
        log.info("KIS_CALL_SUCCESS mock=true method=GET endpoint={} status=200 durationMs={}", endpoint, duration);
        return Map.of("endpoint", endpoint, "tradeMode", tradeMode.name(), "mock", true);
    }

    @Override
    public Map<String, Object> post(KisEndpoint endpoint, Map<String, String> body, KisTradeMode tradeMode) {
        long startedAt = System.currentTimeMillis();
        log.info("KIS_CALL_START mock=true method=POST endpoint={} trId={} tradeMode={} body={}", endpoint.getPath(), endpoint.transactionId(tradeMode), tradeMode, body);
        long duration = System.currentTimeMillis() - startedAt;
        logService.log("POST", endpoint.getPath(), 200, duration, null, null);
        log.info("KIS_CALL_SUCCESS mock=true method=POST endpoint={} trId={} status=200 durationMs={}", endpoint.getPath(), endpoint.transactionId(tradeMode), duration);
        return Map.of("endpoint", endpoint.getPath(), "trId", endpoint.transactionId(tradeMode), "tradeMode", tradeMode.name(), "mock", true);
    }
}
