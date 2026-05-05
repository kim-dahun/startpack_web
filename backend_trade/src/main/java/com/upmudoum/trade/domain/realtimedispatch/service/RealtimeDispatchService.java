package com.upmudoum.trade.domain.realtimedispatch.service;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.realtimepublish.dto.RealtimeEventEnvelopeDto;
import com.upmudoum.trade.domain.realtimepublish.service.RealtimeEnvelopeFactory;
import com.upmudoum.trade.domain.realtimepublish.service.RealtimePublishService;
import org.springframework.stereotype.Service;

@Service
public class RealtimeDispatchService {

    private final RealtimeEnvelopeFactory envelopeFactory;
    private final RealtimePublishService publishService;

    public RealtimeDispatchService(RealtimeEnvelopeFactory envelopeFactory, RealtimePublishService publishService) {
        this.envelopeFactory = envelopeFactory;
        this.publishService = publishService;
    }

    public RealtimeEventEnvelopeDto dispatch(TradeRealtimeEventDto event) {
        RealtimeEventEnvelopeDto envelope = envelopeFactory.from(event);
        publishService.publish(event, envelope);
        return envelope;
    }
}
