package com.upmudoum.trade.domain.kis.vo;

public enum KisEndpoint {
    INQUIRE_BALANCE("/uapi/domestic-stock/v1/trading/inquire-balance", "VTTC8434R", "TTTC8434R"),
    INQUIRE_DAILY_CCLD("/uapi/domestic-stock/v1/trading/inquire-daily-ccld", "VTTC8001R", "TTTC8001R"),
    INQUIRE_PSBL_ORDER("/uapi/domestic-stock/v1/trading/inquire-psbl-order", "VTTC8908R", "TTTC8908R"),
    ORDER_CASH_BUY("/uapi/domestic-stock/v1/trading/order-cash", "VTTC0802U", "TTTC0802U"),
    ORDER_CASH_SELL("/uapi/domestic-stock/v1/trading/order-cash", "VTTC0801U", "TTTC0801U"),
    SEARCH_INFO("/uapi/domestic-stock/v1/quotations/search-info", "CTPF1604R", "CTPF1604R"),
    INQUIRE_PRICE("/uapi/domestic-stock/v1/quotations/inquire-price", "FHKST01010100", "FHKST01010100"),
    INQUIRE_ORDERBOOK("/uapi/domestic-stock/v1/quotations/inquire-asking-price-exp-ccn", "FHKST01010200", "FHKST01010200"),
    INQUIRE_DAILY_ITEM_CHART_PRICE("/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice", "FHKST03010100", "FHKST03010100"),
    RANKING_MARKET_VALUE("/uapi/domestic-stock/v1/ranking/market-value", "FHPST01790000", "FHPST01790000"),
    RANKING_EXP_TRANS_UPDOWN("/uapi/domestic-stock/v1/ranking/exp-trans-updown", "FHPST01820000", "FHPST01820000");

    private final String path;
    private final String paperTransactionId;
    private final String liveTransactionId;

    KisEndpoint(String path, String paperTransactionId, String liveTransactionId) {
        this.path = path;
        this.paperTransactionId = paperTransactionId;
        this.liveTransactionId = liveTransactionId;
    }

    public String getPath() {
        return path;
    }

    public String transactionId(KisTradeMode tradeMode) {
        if (tradeMode == KisTradeMode.LIVE) {
            return liveTransactionId;
        }
        return paperTransactionId;
    }
}
