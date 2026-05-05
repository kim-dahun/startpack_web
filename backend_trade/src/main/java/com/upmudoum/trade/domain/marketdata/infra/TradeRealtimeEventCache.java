package com.upmudoum.trade.domain.marketdata.infra;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TradeRealtimeEventCache {

    private final Map<TradeRealtimeSubscriptionDto, TradeRealtimeEventDto> latestEvents = new ConcurrentHashMap<>();

    public void put(TradeRealtimeEventDto event) {
        latestEvents.put(new TradeRealtimeSubscriptionDto(event.getType(), event.getItemCode()), event);
    }

    public Optional<TradeRealtimeEventDto> get(TradeRealtimeSubscriptionDto subscription) {
        return Optional.ofNullable(latestEvents.get(subscription));
    }

    public Map<TradeRealtimeSubscriptionDto, TradeRealtimeEventDto> snapshot() {
        return Map.copyOf(latestEvents);
    }
}
