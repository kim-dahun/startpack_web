package com.upmudoum.trade.domain.marketdata.service;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeStatusDto;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import com.upmudoum.trade.domain.marketdata.infra.TradeRealtimeEventCache;
import com.upmudoum.trade.domain.marketdata.infra.TradeRealtimePublisher;
import com.upmudoum.trade.domain.realtimeconnection.service.RealtimeConnectionService;
import com.upmudoum.trade.domain.realtimedispatch.service.RealtimeDispatchService;
import com.upmudoum.trade.domain.realtimepublish.service.RealtimeEnvelopeFactory;
import com.upmudoum.trade.domain.realtimesubscription.service.RealtimeSubscriptionService;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class TradeRealtimeService {

    private final TradeRealtimeEventCache cache;
    private final TradeRealtimePublisher publisher;
    private final RealtimeReceiveLogService receiveLogService;
    private final RealtimeSubscriptionService subscriptionService;
    private final RealtimeDispatchService dispatchService;
    private final RealtimeConnectionService connectionService;
    private final RealtimeEnvelopeFactory envelopeFactory;

    public TradeRealtimeService(
            TradeRealtimeEventCache cache,
            TradeRealtimePublisher publisher,
            RealtimeReceiveLogService receiveLogService,
            RealtimeSubscriptionService subscriptionService,
            RealtimeDispatchService dispatchService,
            RealtimeConnectionService connectionService,
            RealtimeEnvelopeFactory envelopeFactory
    ) {
        this.cache = cache;
        this.publisher = publisher;
        this.receiveLogService = receiveLogService;
        this.subscriptionService = subscriptionService;
        this.dispatchService = dispatchService;
        this.connectionService = connectionService;
        this.envelopeFactory = envelopeFactory;
    }

    public void registerSession(WebSocketSession session) {
        publisher.addSession(session);
    }

    public void removeSession(WebSocketSession session) {
        publisher.removeSession(session);
    }

    public void subscribe(WebSocketSession session, TradeRealtimeSubscriptionDto subscription) {
        subscriptionService.subscribe(session, subscription);
        cache.get(subscription).ifPresent(event -> publisher.send(session, envelopeFactory.from(event)));
    }

    public void unsubscribe(WebSocketSession session, TradeRealtimeSubscriptionDto subscription) {
        subscriptionService.unsubscribe(session, subscription);
    }

    public void publish(TradeRealtimeEventDto event) {
        receiveLogService.save(event);
        dispatchService.dispatch(event);
    }

    public void send(WebSocketSession session, Object payload) {
        publisher.send(session, payload);
    }

    public TradeRealtimeStatusDto status() {
        return new TradeRealtimeStatusDto(
                connectionService.isConnected(),
                publisher.sessionCount(),
                subscriptionService.snapshot().values().stream().mapToInt(Set::size).sum(),
                cache.snapshot().size()
        );
    }

    public void heartbeat() {
        connectionService.heartbeat();
    }
}
