package com.upmudoum.trade.domain.realtimepublish.vo;

public final class RealtimeTopic {

    private RealtimeTopic() {
    }

    public static String itemQuote(String itemCode) {
        return "/topic/trade/items/" + itemCode + "/quote";
    }

    public static String itemOrderbook(String itemCode) {
        return "/topic/trade/items/" + itemCode + "/orderbook";
    }

    public static String itemTicks(String itemCode) {
        return "/topic/trade/items/" + itemCode + "/ticks";
    }

    public static String accountOrders(String accountNo) {
        return "/topic/trade/accounts/" + accountNo + "/orders";
    }

    public static String accountPositions(String accountNo) {
        return "/topic/trade/accounts/" + accountNo + "/positions";
    }

    public static String watchlist(String userId) {
        return "/topic/trade/watchlists/" + userId;
    }

    public static String connectionStatus() {
        return "/topic/trade/realtime/connection-status";
    }
}
