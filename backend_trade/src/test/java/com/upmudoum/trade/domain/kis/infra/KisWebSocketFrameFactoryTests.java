package com.upmudoum.trade.domain.kis.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import org.junit.jupiter.api.Test;

class KisWebSocketFrameFactoryTests {

    @Test
    void subscribeFrameContainsApprovalKeyTransactionIdAndItemCode() {
        KisWebSocketFrameFactory frameFactory = new KisWebSocketFrameFactory(new ObjectMapper());

        String frame = frameFactory.subscribe("approval-key", new TradeRealtimeSubscriptionDto(TradeRealtimeEventType.PRICE, "005930"));

        assertThat(frame)
                .contains("\"approval_key\":\"approval-key\"")
                .contains("\"tr_type\":\"1\"")
                .contains("\"tr_id\":\"H0STCNT0\"")
                .contains("\"tr_key\":\"005930\"");
    }
}
