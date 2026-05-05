package com.upmudoum.trade.domain.marketdata.infra;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class TradeRealtimeSubscriptionRegistry {

    private final Map<String, Set<TradeRealtimeSubscriptionDto>> subscriptionsBySession = new ConcurrentHashMap<>();

    public void subscribe(WebSocketSession session, TradeRealtimeSubscriptionDto subscription) {
        subscriptionsBySession
                .computeIfAbsent(session.getId(), ignored -> ConcurrentHashMap.newKeySet())
                .add(subscription);
    }

    public void unsubscribe(WebSocketSession session, TradeRealtimeSubscriptionDto subscription) {
        Set<TradeRealtimeSubscriptionDto> subscriptions = subscriptionsBySession.get(session.getId());
        if (subscriptions == null) {
            return;
        }
        subscriptions.remove(subscription);
        if (subscriptions.isEmpty()) {
            subscriptionsBySession.remove(session.getId());
        }
    }

    public void removeSession(WebSocketSession session) {
        subscriptionsBySession.remove(session.getId());
    }

    public boolean isSubscribed(String sessionId, TradeRealtimeEventDto event) {
        return subscriptionsBySession
                .getOrDefault(sessionId, Collections.emptySet())
                .contains(new TradeRealtimeSubscriptionDto(event.getType(), event.getItemCode()));
    }

    public Map<String, Set<TradeRealtimeSubscriptionDto>> snapshot() {
        return Map.copyOf(subscriptionsBySession);
    }
}
