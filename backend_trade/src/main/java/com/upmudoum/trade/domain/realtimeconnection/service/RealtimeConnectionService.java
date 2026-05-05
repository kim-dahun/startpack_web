package com.upmudoum.trade.domain.realtimeconnection.service;

import com.upmudoum.trade.domain.kis.infra.KisRealtimeClient;
import com.upmudoum.trade.domain.realtimepublish.dto.RealtimeEventEnvelopeDto;
import com.upmudoum.trade.domain.realtimepublish.service.RealtimeEnvelopeFactory;
import com.upmudoum.trade.domain.realtimepublish.service.RealtimePublishService;
import com.upmudoum.trade.domain.realtimesubscription.service.RealtimeSubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class RealtimeConnectionService {

    private final KisRealtimeClient kisRealtimeClient;
    private final RealtimeSubscriptionService subscriptionService;
    private final RealtimeEnvelopeFactory envelopeFactory;
    private final RealtimePublishService publishService;

    public RealtimeConnectionService(
            KisRealtimeClient kisRealtimeClient,
            RealtimeSubscriptionService subscriptionService,
            RealtimeEnvelopeFactory envelopeFactory,
            RealtimePublishService publishService
    ) {
        this.kisRealtimeClient = kisRealtimeClient;
        this.subscriptionService = subscriptionService;
        this.envelopeFactory = envelopeFactory;
        this.publishService = publishService;
    }

    public boolean isConnected() {
        return kisRealtimeClient.isConnected();
    }

    public RealtimeEventEnvelopeDto heartbeat() {
        RealtimeEventEnvelopeDto envelope = envelopeFactory.connectionStatus(isConnected(), subscriptionService.subscriptionCount());
        publishService.publish(envelope);
        return envelope;
    }
}
