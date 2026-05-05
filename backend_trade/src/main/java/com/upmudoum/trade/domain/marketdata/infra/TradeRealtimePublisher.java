package com.upmudoum.trade.domain.marketdata.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.realtimepublish.dto.RealtimeEventEnvelopeDto;
import com.upmudoum.trade.domain.realtimepublish.service.RealtimeEnvelopeFactory;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class TradeRealtimePublisher {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final TradeRealtimeSubscriptionRegistry registry;
    private final TradeRealtimeEventCache cache;
    private final ObjectMapper objectMapper;
    private final RealtimeEnvelopeFactory envelopeFactory;

    public TradeRealtimePublisher(
            TradeRealtimeSubscriptionRegistry registry,
            TradeRealtimeEventCache cache,
            ObjectMapper objectMapper,
            RealtimeEnvelopeFactory envelopeFactory
    ) {
        this.registry = registry;
        this.cache = cache;
        this.objectMapper = objectMapper;
        this.envelopeFactory = envelopeFactory;
    }

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
        registry.removeSession(session);
    }

    public void publish(TradeRealtimeEventDto event) {
        RealtimeEventEnvelopeDto envelope = envelopeFactory.from(event);
        publishEnvelope(event, envelope);
    }

    public void publishEnvelope(TradeRealtimeEventDto event, RealtimeEventEnvelopeDto envelope) {
        cache.put(event);
        sessions.stream()
                .filter(WebSocketSession::isOpen)
                .filter(session -> registry.isSubscribed(session.getId(), event))
                .forEach(session -> send(session, envelope));
    }

    public void publishEnvelope(RealtimeEventEnvelopeDto envelope) {
        sessions.stream()
                .filter(WebSocketSession::isOpen)
                .forEach(session -> send(session, envelope));
    }

    public int sessionCount() {
        return sessions.size();
    }

    public void send(WebSocketSession session, Object payload) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            }
        } catch (IOException ex) {
            removeSession(session);
        }
    }
}
