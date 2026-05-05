package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "trade.kis.realtime-client.enabled", havingValue = "false", matchIfMissing = true)
public class NoopKisRealtimeClient implements KisRealtimeClient {

    private final AtomicBoolean connected = new AtomicBoolean(false);

    @Override
    public void connect() {
        connected.set(true);
    }

    @Override
    public void disconnect() {
        connected.set(false);
    }

    @Override
    public void subscribe(TradeRealtimeSubscriptionDto subscription) {
        connect();
    }

    @Override
    public void unsubscribe(TradeRealtimeSubscriptionDto subscription) {
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }
}
