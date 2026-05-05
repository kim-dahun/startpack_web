package com.upmudoum.trade.domain.kis.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.kis.vo.KisProperties;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class KisQueryFactoryTests {

    private final KisQueryFactory queryFactory = new KisQueryFactory(
            new KisProperties("app-key", "app-secret", "http://paper", "http://live", "ws://paper", "ws://live", "01")
    );

    @Test
    void balanceUsesKisAccountParameterNames() {
        assertThat(queryFactory.balance("12345678-01"))
                .containsEntry("CANO", "12345678")
                .containsEntry("ACNT_PRDT_CD", "01")
                .containsEntry("INQR_DVSN", "02");
    }

    @Test
    void dailyCcldUsesKisDateAndAccountParameterNames() {
        assertThat(queryFactory.dailyCcld("1234567801", LocalDate.of(2026, 5, 1)))
                .containsEntry("CANO", "12345678")
                .containsEntry("ACNT_PRDT_CD", "01")
                .containsEntry("INQR_STRT_DT", "20260501")
                .containsEntry("INQR_END_DT", "20260501");
    }

    @Test
    void itemPriceUsesKisQuotationParameterNames() {
        assertThat(queryFactory.itemPrice("005930"))
                .containsEntry("FID_COND_MRKT_DIV_CODE", "J")
                .containsEntry("FID_INPUT_ISCD", "005930");
    }

    @Test
    void orderableAmountUsesKisTradingParameterNames() {
        assertThat(queryFactory.orderableAmount("1234567801", "005930", "70000"))
                .containsEntry("CANO", "12345678")
                .containsEntry("ACNT_PRDT_CD", "01")
                .containsEntry("PDNO", "005930")
                .containsEntry("ORD_UNPR", "70000");
    }

    @Test
    void cashOrderUsesKisTradingBodyNames() {
        assertThat(queryFactory.cashOrder("1234567801", "005930", 3, "70000"))
                .containsEntry("CANO", "12345678")
                .containsEntry("ACNT_PRDT_CD", "01")
                .containsEntry("PDNO", "005930")
                .containsEntry("ORD_QTY", "3")
                .containsEntry("ORD_UNPR", "70000");
    }
}
