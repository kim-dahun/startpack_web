package com.upmudoum.trade.domain.kis.infra;

import com.upmudoum.trade.domain.kis.vo.KisProperties;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KisQueryFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

    private final KisProperties kisProperties;

    public KisQueryFactory(KisProperties kisProperties) {
        this.kisProperties = kisProperties;
    }

    public Map<String, String> balance(String accountNo) {
        AccountParts accountParts = accountParts(accountNo);
        return Map.ofEntries(
                Map.entry("CANO", accountParts.cano()),
                Map.entry("ACNT_PRDT_CD", accountParts.productCode()),
                Map.entry("AFHR_FLPR_YN", "N"),
                Map.entry("OFL_YN", ""),
                Map.entry("INQR_DVSN", "02"),
                Map.entry("UNPR_DVSN", "01"),
                Map.entry("FUND_STTL_ICLD_YN", "N"),
                Map.entry("FNCG_AMT_AUTO_RDPT_YN", "N"),
                Map.entry("PRCS_DVSN", "00"),
                Map.entry("CTX_AREA_FK100", ""),
                Map.entry("CTX_AREA_NK100", "")
        );
    }

    public Map<String, String> dailyCcld(String accountNo, LocalDate baseDate) {
        AccountParts accountParts = accountParts(accountNo);
        String date = baseDate.format(DATE_FORMATTER);
        return Map.ofEntries(
                Map.entry("CANO", accountParts.cano()),
                Map.entry("ACNT_PRDT_CD", accountParts.productCode()),
                Map.entry("INQR_STRT_DT", date),
                Map.entry("INQR_END_DT", date),
                Map.entry("SLL_BUY_DVSN_CD", "00"),
                Map.entry("INQR_DVSN", "00"),
                Map.entry("PDNO", ""),
                Map.entry("CCLD_DVSN", "00"),
                Map.entry("ORD_GNO_BRNO", ""),
                Map.entry("ODNO", ""),
                Map.entry("INQR_DVSN_3", "00"),
                Map.entry("INQR_DVSN_1", ""),
                Map.entry("CTX_AREA_FK100", ""),
                Map.entry("CTX_AREA_NK100", "")
        );
    }

    public Map<String, String> itemPrice(String itemCode) {
        return Map.of(
                "FID_COND_MRKT_DIV_CODE", "J",
                "FID_INPUT_ISCD", itemCode
        );
    }

    public Map<String, String> itemSearch(String keyword) {
        return Map.of(
                "PRDT_TYPE_CD", "300",
                "PDNO", keyword == null ? "" : keyword
        );
    }

    public Map<String, String> orderbook(String itemCode) {
        return Map.of(
                "FID_COND_MRKT_DIV_CODE", "J",
                "FID_INPUT_ISCD", itemCode
        );
    }

    public Map<String, String> itemChart(String itemCode, String periodType, LocalDate from, LocalDate to) {
        return Map.ofEntries(
                Map.entry("FID_COND_MRKT_DIV_CODE", "J"),
                Map.entry("FID_INPUT_ISCD", itemCode),
                Map.entry("FID_INPUT_DATE_1", from.format(DATE_FORMATTER)),
                Map.entry("FID_INPUT_DATE_2", to.format(DATE_FORMATTER)),
                Map.entry("FID_PERIOD_DIV_CODE", periodType),
                Map.entry("FID_ORG_ADJ_PRC", "0")
        );
    }

    public Map<String, String> rankingMarketValue(String marketCode, String rankSortCode) {
        return Map.ofEntries(
                Map.entry("FID_TRGT_CLS_CODE", "0"),
                Map.entry("FID_COND_MRKT_DIV_CODE", "J"),
                Map.entry("FID_COND_SCR_DIV_CODE", "20179"),
                Map.entry("FID_INPUT_ISCD", rankingMarketInputCode(marketCode)),
                Map.entry("FID_DIV_CLS_CODE", "0"),
                Map.entry("FID_INPUT_PRICE_1", ""),
                Map.entry("FID_INPUT_PRICE_2", ""),
                Map.entry("FID_VOL_CNT", ""),
                Map.entry("FID_INPUT_OPTION_1", String.valueOf(LocalDate.now().getYear())),
                Map.entry("FID_INPUT_OPTION_2", "3"),
                Map.entry("FID_RANK_SORT_CLS_CODE", rankSortCode),
                Map.entry("FID_BLNG_CLS_CODE", "0"),
                Map.entry("FID_TRGT_EXLS_CLS_CODE", "0")
        );
    }

    public Map<String, String> rankingExpTransUpdown(String marketCode, String rankSortCode) {
        return Map.ofEntries(
                Map.entry("FID_RANK_SORT_CLS_CODE", rankSortCode),
                Map.entry("FID_COND_MRKT_DIV_CODE", "J"),
                Map.entry("FID_COND_SCR_DIV_CODE", "20182"),
                Map.entry("FID_INPUT_ISCD", rankingMarketInputCode(marketCode)),
                Map.entry("FID_DIV_CLS_CODE", "0"),
                Map.entry("FID_APLY_RANG_PRC_1", ""),
                Map.entry("FID_VOL_CNT", ""),
                Map.entry("FID_PBMN", ""),
                Map.entry("FID_BLNG_CLS_CODE", "0"),
                Map.entry("FID_MKOP_CLS_CODE", "0")
        );
    }

    private String rankingMarketInputCode(String marketCode) {
        if ("KOSPI".equalsIgnoreCase(marketCode)) {
            return "0001";
        }
        if ("KOSDAQ".equalsIgnoreCase(marketCode)) {
            return "1001";
        }
        return "0000";
    }

    public Map<String, String> orderableAmount(String accountNo, String itemCode, String orderPrice) {
        AccountParts accountParts = accountParts(accountNo);
        return Map.ofEntries(
                Map.entry("CANO", accountParts.cano()),
                Map.entry("ACNT_PRDT_CD", accountParts.productCode()),
                Map.entry("PDNO", itemCode),
                Map.entry("ORD_UNPR", orderPrice),
                Map.entry("ORD_DVSN", "00"),
                Map.entry("CMA_EVLU_AMT_ICLD_YN", "N"),
                Map.entry("OVRS_ICLD_YN", "N")
        );
    }

    public Map<String, String> cashOrder(String accountNo, String itemCode, long quantity, String orderPrice) {
        AccountParts accountParts = accountParts(accountNo);
        return Map.of(
                "CANO", accountParts.cano(),
                "ACNT_PRDT_CD", accountParts.productCode(),
                "PDNO", itemCode,
                "ORD_DVSN", "00",
                "ORD_QTY", String.valueOf(quantity),
                "ORD_UNPR", orderPrice
        );
    }

    private AccountParts accountParts(String accountNo) {
        if (accountNo == null || accountNo.isBlank()) {
            return new AccountParts("", kisProperties.getAccountProductCode());
        }
        String normalized = accountNo.trim().replace("-", "");
        if (normalized.length() > 8) {
            return new AccountParts(normalized.substring(0, 8), normalized.substring(8));
        }
        return new AccountParts(normalized, kisProperties.getAccountProductCode());
    }

    private static class AccountParts {

        private final String cano;
        private final String productCode;

        AccountParts(String cano, String productCode) {
            this.cano = cano;
            this.productCode = productCode;
        }

        String cano() {
            return cano;
        }

        String productCode() {
            return productCode;
        }
    }
}
