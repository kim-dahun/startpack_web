package com.upmudoum.trade.domain.realtimesubscription.service;

import com.upmudoum.trade.domain.kis.infra.KisRealtimeClient;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import com.upmudoum.trade.domain.marketdata.infra.TradeRealtimeSubscriptionRegistry;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class RealtimeSubscriptionService {

    private final TradeRealtimeSubscriptionRegistry registry;
    private final KisRealtimeClient kisRealtimeClient;

    public RealtimeSubscriptionService(TradeRealtimeSubscriptionRegistry registry, KisRealtimeClient kisRealtimeClient) {
        this.registry = registry;
        this.kisRealtimeClient = kisRealtimeClient;
    }

    public void subscribe(WebSocketSession session, TradeRealtimeSubscriptionDto subscription) {
        registry.subscribe(session, subscription);
        kisRealtimeClient.subscribe(subscription);
    }

    public void unsubscribe(WebSocketSession session, TradeRealtimeSubscriptionDto subscription) {
        registry.unsubscribe(session, subscription);
        kisRealtimeClient.unsubscribe(subscription);
    }

    public void removeSession(WebSocketSession session) {
        registry.removeSession(session);
    }

    public Map<String, Set<TradeRealtimeSubscriptionDto>> snapshot() {
        return registry.snapshot();
    }

    public int subscriptionCount() {
        return registry.snapshot().values().stream().mapToInt(Set::size).sum();
    }
}
