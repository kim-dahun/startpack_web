package com.upmudoum.trade.domain.realtimepublish.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import com.upmudoum.trade.domain.realtimepublish.dto.RealtimeEventEnvelopeDto;
import com.upmudoum.trade.domain.realtimepublish.vo.RealtimeEventType;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RealtimeEnvelopeFactoryTests {

    private final RealtimeEnvelopeFactory factory = new RealtimeEnvelopeFactory();

    @Test
    void convertsQuoteEventToStandardEnvelope() {
        TradeRealtimeEventDto event = new TradeRealtimeEventDto();
        event.setType(TradeRealtimeEventType.PRICE);
        event.setItemCode("005930");
        event.setOccurredAt(Instant.parse("2026-05-04T00:00:00Z"));
        event.setPayload(Map.of("currentPrice", 70000));

        RealtimeEventEnvelopeDto envelope = factory.from(event);

        assertThat(envelope.getEventType()).isEqualTo(RealtimeEventType.QUOTE_TICK);
        assertThat(envelope.getTopic()).isEqualTo("/topic/trade/items/005930/quote");
        assertThat(envelope.getSequenceNo()).isPositive();
        assertThat(envelope.getPayload()).containsEntry("currentPrice", 70000);
    }

    @Test
    void routesOrderEventByAccount() {
        TradeRealtimeEventDto event = new TradeRealtimeEventDto();
        event.setType(TradeRealtimeEventType.ORDER_FILLED);
        event.setItemCode("005930");
        event.setPayload(Map.of("accountNo", "12345678-01", "orderNo", "O1"));

        RealtimeEventEnvelopeDto envelope = factory.from(event);

        assertThat(envelope.getEventType()).isEqualTo(RealtimeEventType.ORDER_FILLED);
        assertThat(envelope.getTopic()).isEqualTo("/topic/trade/accounts/12345678-01/orders");
        assertThat(envelope.getAccountNo()).isEqualTo("12345678-01");
    }
}
