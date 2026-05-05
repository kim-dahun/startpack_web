package com.upmudoum.trade.domain.account.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class KisAccountMapperTests {

    private final KisAccountMapper mapper = new KisAccountMapper();

    @Test
    void toAccountSummariesMapsKisOutput2() {
        Map<String, Object> response = Map.of("output2", List.of(Map.of(
                "tot_evlu_amt", "10000",
                "dnca_tot_amt", "3000"
        )));

        assertThat(mapper.toAccountSummaries(response))
                .hasSize(1)
                .first()
                .extracting("totalAssetAmount", "cashAmount")
                .containsExactly(new java.math.BigDecimal("10000"), new java.math.BigDecimal("3000"));
    }

    @Test
    void toDailyBalancesMapsKisOutput1() {
        Map<String, Object> response = Map.of("output1", List.of(Map.of(
                "tot_evlu_amt", "10000",
                "evlu_pfls_smtl_amt", "-500"
        )));

        assertThat(mapper.toDailyBalances("paper-account", LocalDate.of(2026, 5, 1), response))
                .hasSize(1)
                .first()
                .extracting("accountNo", "baseDate")
                .containsExactly("paper-account", LocalDate.of(2026, 5, 1));
    }

    @Test
    void toPositionsMapsKisOutput1() {
        Map<String, Object> response = Map.of("output1", List.of(Map.of(
                "pdno", "005930",
                "prdt_name", "Samsung Electronics",
                "hldg_qty", "10",
                "ord_psbl_qty", "7",
                "pchs_avg_pric", "70000",
                "prpr", "72000",
                "evlu_amt", "720000",
                "evlu_pfls_amt", "20000",
                "evlu_pfls_rt", "2.85"
        )));

        assertThat(mapper.toPositions("12345678-01", response))
                .hasSize(1)
                .first()
                .extracting("accountNo", "itemCode", "quantity", "orderableQuantity")
                .containsExactly("12345678-01", "005930", 10L, 7L);
    }

    @Test
    void toPositionsFallsBackOrderableQuantityToHoldingQuantity() {
        Map<String, Object> response = Map.of("output1", List.of(Map.of(
                "pdno", "005930",
                "prdt_name", "Samsung Electronics",
                "hldg_qty", "10"
        )));

        assertThat(mapper.toPositions("12345678-01", response))
                .hasSize(1)
                .first()
                .extracting("quantity", "orderableQuantity")
                .containsExactly(10L, 10L);
    }
}
