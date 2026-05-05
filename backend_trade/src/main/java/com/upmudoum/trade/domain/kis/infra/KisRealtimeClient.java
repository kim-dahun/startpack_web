package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeSubscriptionDto;

public interface KisRealtimeClient {

    void connect();

    void disconnect();

    void subscribe(TradeRealtimeSubscriptionDto subscription);

    void unsubscribe(TradeRealtimeSubscriptionDto subscription);

    boolean isConnected();
}
