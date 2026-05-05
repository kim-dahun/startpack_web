package com.upmudoum.trade.domain.realtimepublish.service;

import com.upmudoum.trade.domain.realtimepublish.dto.RealtimeEventEnvelopeDto;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class RealtimePublishService {

    private final com.upmudoum.trade.domain.marketdata.infra.TradeRealtimePublisher publisher;

    public RealtimePublishService(com.upmudoum.trade.domain.marketdata.infra.TradeRealtimePublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(RealtimeEventEnvelopeDto envelope) {
        publisher.publishEnvelope(envelope);
    }

    public void publish(TradeRealtimeEventDto event) {
        publisher.publish(event);
    }

    public void publish(TradeRealtimeEventDto event, RealtimeEventEnvelopeDto envelope) {
        publisher.publishEnvelope(event, envelope);
    }

    public void send(WebSocketSession session, RealtimeEventEnvelopeDto envelope) {
        publisher.send(session, envelope);
    }
}
