package com.upmudoum.trade.domain.kis.vo;

import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;

public enum KisRealtimeTransactionId {
    PRICE(TradeRealtimeEventType.PRICE, "H0STCNT0"),
    ORDERBOOK(TradeRealtimeEventType.ORDERBOOK, "H0STASP0"),
    BALANCE(TradeRealtimeEventType.BALANCE, "H0STCNI0");

    private final TradeRealtimeEventType eventType;
    private final String transactionId;

    KisRealtimeTransactionId(TradeRealtimeEventType eventType, String transactionId) {
        this.eventType = eventType;
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public static String from(TradeRealtimeEventType eventType) {
        for (KisRealtimeTransactionId value : values()) {
            if (value.eventType == eventType) {
                return value.transactionId;
            }
        }
        throw new IllegalArgumentException("unsupported realtime event type: " + eventType);
    }

    public static TradeRealtimeEventType eventTypeOf(String transactionId) {
        for (KisRealtimeTransactionId value : values()) {
            if (value.transactionId.equals(transactionId)) {
                return value.eventType;
            }
        }
        throw new IllegalArgumentException("unsupported realtime transaction id: " + transactionId);
    }
}
