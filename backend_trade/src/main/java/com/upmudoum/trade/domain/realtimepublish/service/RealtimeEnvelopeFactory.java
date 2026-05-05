package com.upmudoum.trade.domain.realtimepublish.service;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.realtimepublish.dto.RealtimeEventEnvelopeDto;
import com.upmudoum.trade.domain.realtimepublish.vo.RealtimeEventType;
import com.upmudoum.trade.domain.realtimepublish.vo.RealtimeTopic;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class RealtimeEnvelopeFactory {

    private final AtomicLong sequence = new AtomicLong();

    public RealtimeEventEnvelopeDto from(TradeRealtimeEventDto event) {
        RealtimeEventType eventType = RealtimeEventType.fromLegacy(event.getType());
        RealtimeEventEnvelopeDto envelope = new RealtimeEventEnvelopeDto();
        envelope.setEventType(eventType);
        envelope.setItemCode(event.getItemCode());
        envelope.setAccountNo(accountNo(event.getPayload()));
        envelope.setOccurredAt(event.getOccurredAt() == null ? Instant.now() : event.getOccurredAt());
        envelope.setSequenceNo(sequence.incrementAndGet());
        envelope.setPayload(event.getPayload() == null ? Map.of() : event.getPayload());
        envelope.setTopic(topic(eventType, envelope.getItemCode(), envelope.getAccountNo(), envelope.getPayload()));
        return envelope;
    }

    public RealtimeEventEnvelopeDto connectionStatus(boolean connected, int subscriptionCount) {
        RealtimeEventEnvelopeDto envelope = new RealtimeEventEnvelopeDto();
        envelope.setEventType(RealtimeEventType.REALTIME_CONNECTION_STATUS);
        envelope.setTopic(RealtimeTopic.connectionStatus());
        envelope.setOccurredAt(Instant.now());
        envelope.setSequenceNo(sequence.incrementAndGet());
        envelope.setPayload(Map.of("connected", connected, "subscriptionCount", subscriptionCount));
        return envelope;
    }

    private String topic(RealtimeEventType eventType, String itemCode, String accountNo, Map<String, Object> payload) {
        return switch (eventType) {
            case QUOTE_TICK -> RealtimeTopic.itemQuote(itemCode);
            case TRADE_TICK -> RealtimeTopic.itemTicks(itemCode);
            case ORDERBOOK_SNAPSHOT, ORDERBOOK_DELTA -> RealtimeTopic.itemOrderbook(itemCode);
            case ACCOUNT_BALANCE_CHANGED, POSITION_CHANGED -> RealtimeTopic.accountPositions(required(accountNo, "accountNo"));
            case ORDER_ACCEPTED, ORDER_REJECTED, ORDER_PARTIALLY_FILLED, ORDER_FILLED -> RealtimeTopic.accountOrders(required(accountNo, "accountNo"));
            case WATCHLIST_CHANGED -> RealtimeTopic.watchlist(required(text(payload, "userId"), "userId"));
            case REALTIME_CONNECTION_STATUS -> RealtimeTopic.connectionStatus();
        };
    }

    private String accountNo(Map<String, Object> payload) {
        return text(payload, "accountNo");
    }

    private String text(Map<String, Object> payload, String key) {
        if (payload == null) {
            return null;
        }
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }
        String text = value.toString();
        return text.isBlank() ? null : text;
    }

    private String required(String value, String name) {
        if (value == null || value.isBlank()) {
            return "unknown-" + name;
        }
        return value;
    }
}
