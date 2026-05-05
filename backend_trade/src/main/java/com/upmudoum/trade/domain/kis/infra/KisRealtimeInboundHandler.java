package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.realtimedispatch.service.RealtimeDispatchService;
import com.upmudoum.trade.domain.marketdata.service.RealtimeReceiveLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KisRealtimeInboundHandler {

    private static final Logger log = LoggerFactory.getLogger(KisRealtimeInboundHandler.class);

    private final KisRealtimeMessageParser parser;
    private final RealtimeReceiveLogService receiveLogService;
    private final RealtimeDispatchService dispatchService;

    public KisRealtimeInboundHandler(
            KisRealtimeMessageParser parser,
            RealtimeReceiveLogService receiveLogService,
            RealtimeDispatchService dispatchService
    ) {
        this.parser = parser;
        this.receiveLogService = receiveLogService;
        this.dispatchService = dispatchService;
    }

    public void handle(String rawMessage) {
        parser.parse(rawMessage).ifPresent(event -> {
            receiveLogService.save(event);
            dispatchService.dispatch(event);
            log.info("KIS_REALTIME_RECEIVE type={} itemCode={}", event.getType(), event.getItemCode());
        });
    }
}
