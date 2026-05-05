package com.upmudoum.trade.domain.realtimepublish.vo;

import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;

public enum RealtimeEventType {
    QUOTE_TICK,
    TRADE_TICK,
    ORDERBOOK_SNAPSHOT,
    ORDERBOOK_DELTA,
    ACCOUNT_BALANCE_CHANGED,
    POSITION_CHANGED,
    ORDER_ACCEPTED,
    ORDER_REJECTED,
    ORDER_PARTIALLY_FILLED,
    ORDER_FILLED,
    WATCHLIST_CHANGED,
    REALTIME_CONNECTION_STATUS;

    public static RealtimeEventType fromLegacy(TradeRealtimeEventType type) {
        if (type == null) {
            return REALTIME_CONNECTION_STATUS;
        }
        return switch (type) {
            case PRICE -> QUOTE_TICK;
            case ORDERBOOK -> ORDERBOOK_SNAPSHOT;
            case BALANCE -> ACCOUNT_BALANCE_CHANGED;
            case QUOTE_TICK -> QUOTE_TICK;
            case TRADE_TICK -> TRADE_TICK;
            case ORDERBOOK_SNAPSHOT -> ORDERBOOK_SNAPSHOT;
            case ORDERBOOK_DELTA -> ORDERBOOK_DELTA;
            case ACCOUNT_BALANCE_CHANGED -> ACCOUNT_BALANCE_CHANGED;
            case POSITION_CHANGED -> POSITION_CHANGED;
            case ORDER_ACCEPTED -> ORDER_ACCEPTED;
            case ORDER_REJECTED -> ORDER_REJECTED;
            case ORDER_PARTIALLY_FILLED -> ORDER_PARTIALLY_FILLED;
            case ORDER_FILLED -> ORDER_FILLED;
            case WATCHLIST_CHANGED -> WATCHLIST_CHANGED;
            case REALTIME_CONNECTION_STATUS -> REALTIME_CONNECTION_STATUS;
        };
    }
}
