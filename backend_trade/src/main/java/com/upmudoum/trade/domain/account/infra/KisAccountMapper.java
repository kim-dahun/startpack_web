package com.upmudoum.trade.domain.account.infra;

import com.upmudoum.trade.domain.account.dto.AccountSummaryDto;
import com.upmudoum.trade.domain.account.dto.AccountBalanceDetailDto;
import com.upmudoum.trade.domain.account.dto.DailyBalanceDto;
import com.upmudoum.trade.domain.account.dto.PositionDto;
import com.upmudoum.trade.domain.kis.infra.KisResponseExtractor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KisAccountMapper {

    public List<AccountSummaryDto> toAccountSummaries(Map<String, Object> response) {
        List<Map<String, Object>> output2 = KisResponseExtractor.list(response, "output2");
        if (output2.isEmpty()) {
            return List.of();
        }
        return output2.stream()
                .map(row -> new AccountSummaryDto(
                        KisResponseExtractor.text(row, "accountNo", "cano"),
                        "KIS 계좌",
                        KisResponseExtractor.decimal(row, "tot_evlu_amt", "totalAssetAmount"),
                        KisResponseExtractor.decimal(row, "dnca_tot_amt", "cashAmount")
                ))
                .toList();
    }

    public AccountSummaryDto toAccountSummary(String accountNo, Map<String, Object> response) {
        AccountBalanceDetailDto detail = toBalanceDetail(accountNo, response);
        return new AccountSummaryDto(
                accountNo,
                "KIS 실계좌",
                detail.getTotalAssetAmount(),
                detail.getCashAmount()
        );
    }

    public List<DailyBalanceDto> toDailyBalances(String accountNo, LocalDate baseDate, Map<String, Object> response) {
        List<Map<String, Object>> output1 = KisResponseExtractor.list(response, "output1");
        if (output1.isEmpty()) {
            return List.of();
        }
        return output1.stream()
                .map(row -> new DailyBalanceDto(
                        accountNo,
                        baseDate,
                        KisResponseExtractor.decimal(row, "tot_evlu_amt", "totalAssetAmount"),
                        KisResponseExtractor.decimal(row, "evlu_pfls_smtl_amt", "profitLossAmount")
                ))
                .toList();
    }

    public AccountBalanceDetailDto toBalanceDetail(String accountNo, Map<String, Object> response) {
        Map<String, Object> output2 = output2(response);
        AccountBalanceDetailDto dto = new AccountBalanceDetailDto();
        dto.setAccountNo(accountNo);
        dto.setTotalAssetAmount(KisResponseExtractor.decimal(output2, "tot_evlu_amt", "totalAssetAmount"));
        dto.setCashAmount(KisResponseExtractor.decimal(output2, "dnca_tot_amt", "cashAmount"));
        dto.setOrderableCashAmount(KisResponseExtractor.decimal(output2, "ord_psbl_cash", "orderableCashAmount"));
        dto.setTotalEvaluationAmount(KisResponseExtractor.decimal(output2, "scts_evlu_amt", "totalEvaluationAmount"));
        dto.setTotalProfitLossAmount(KisResponseExtractor.decimal(output2, "evlu_pfls_smtl_amt", "totalProfitLossAmount"));
        dto.setTotalProfitLossRate(KisResponseExtractor.decimal(output2, "asst_icdc_erng_rt", "totalProfitLossRate"));
        dto.setPositions(toPositions(accountNo, response));
        return dto;
    }

    private Map<String, Object> output2(Map<String, Object> response) {
        Map<String, Object> object = KisResponseExtractor.object(response, "output2");
        if (!object.isEmpty()) {
            return object;
        }
        List<Map<String, Object>> list = KisResponseExtractor.list(response, "output2");
        return list.isEmpty() ? Map.of() : list.getFirst();
    }

    public List<PositionDto> toPositions(String accountNo, Map<String, Object> response) {
        return KisResponseExtractor.list(response, "output1").stream()
                .map(row -> toPosition(accountNo, row))
                .toList();
    }

    private PositionDto toPosition(String accountNo, Map<String, Object> row) {
        PositionDto dto = new PositionDto();
        dto.setAccountNo(accountNo);
        dto.setItemCode(KisResponseExtractor.text(row, "pdno", "itemCode"));
        dto.setItemName(KisResponseExtractor.text(row, "prdt_name", "itemName"));
        long quantity = KisResponseExtractor.decimal(row, "hldg_qty", "quantity").longValue();
        dto.setQuantity(quantity);
        dto.setOrderableQuantity(orderableQuantity(row, quantity));
        dto.setAveragePrice(KisResponseExtractor.decimal(row, "pchs_avg_pric", "averagePrice"));
        dto.setCurrentPrice(KisResponseExtractor.decimal(row, "prpr", "currentPrice"));
        dto.setEvaluationAmount(KisResponseExtractor.decimal(row, "evlu_amt", "evaluationAmount"));
        dto.setProfitLossAmount(KisResponseExtractor.decimal(row, "evlu_pfls_amt", "profitLossAmount"));
        dto.setProfitLossRate(KisResponseExtractor.decimal(row, "evlu_pfls_rt", "profitLossRate"));
        return dto;
    }

    private long orderableQuantity(Map<String, Object> row, long fallbackQuantity) {
        java.math.BigDecimal value = KisResponseExtractor.decimal(
                row,
                "ord_psbl_qty",
                "ord_psbl_sll_qty",
                "sll_psbl_qty",
                "sellableQuantity",
                "orderableQuantity"
        );
        if (value.compareTo(java.math.BigDecimal.ZERO) > 0) {
            return value.longValue();
        }
        return fallbackQuantity;
    }
}
