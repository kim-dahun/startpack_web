package com.upmudoum.trade.domain.kis.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class KisEndpointTests {

    @Test
    void transactionIdChangesByTradeModeForTradingEndpoints() {
        assertThat(KisEndpoint.INQUIRE_BALANCE.transactionId(KisTradeMode.PAPER)).isEqualTo("VTTC8434R");
        assertThat(KisEndpoint.INQUIRE_BALANCE.transactionId(KisTradeMode.LIVE)).isEqualTo("TTTC8434R");
        assertThat(KisEndpoint.INQUIRE_PSBL_ORDER.transactionId(KisTradeMode.PAPER)).isEqualTo("VTTC8908R");
        assertThat(KisEndpoint.INQUIRE_PSBL_ORDER.transactionId(KisTradeMode.LIVE)).isEqualTo("TTTC8908R");
        assertThat(KisEndpoint.ORDER_CASH_BUY.transactionId(KisTradeMode.PAPER)).isEqualTo("VTTC0802U");
        assertThat(KisEndpoint.ORDER_CASH_BUY.transactionId(KisTradeMode.LIVE)).isEqualTo("TTTC0802U");
        assertThat(KisEndpoint.ORDER_CASH_SELL.transactionId(KisTradeMode.PAPER)).isEqualTo("VTTC0801U");
        assertThat(KisEndpoint.ORDER_CASH_SELL.transactionId(KisTradeMode.LIVE)).isEqualTo("TTTC0801U");
    }

    @Test
    void quotationEndpointUsesSameTransactionIdForBothModes() {
        assertThat(KisEndpoint.INQUIRE_PRICE.transactionId(KisTradeMode.PAPER)).isEqualTo("FHKST01010100");
        assertThat(KisEndpoint.INQUIRE_PRICE.transactionId(KisTradeMode.LIVE)).isEqualTo("FHKST01010100");
        assertThat(KisEndpoint.RANKING_MARKET_VALUE.transactionId(KisTradeMode.LIVE)).isEqualTo("FHPST01790000");
        assertThat(KisEndpoint.RANKING_EXP_TRANS_UPDOWN.transactionId(KisTradeMode.LIVE)).isEqualTo("FHPST01820000");
    }
}
