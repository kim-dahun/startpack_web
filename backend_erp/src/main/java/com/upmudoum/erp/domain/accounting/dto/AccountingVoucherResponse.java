package com.upmudoum.erp.domain.accounting.dto;

import com.upmudoum.erp.domain.accounting.entity.AccountingVoucher;
import com.upmudoum.erp.domain.accounting.vo.VoucherStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountingVoucherResponse {

    private Long id;
    private String voucherNo;
    private LocalDate voucherDate;
    private BigDecimal amount;
    private String currencyCode;
    private String sourceEventType;
    private String sourceEventId;
    private VoucherStatus status;

    public static AccountingVoucherResponse from(AccountingVoucher voucher) {
        AccountingVoucherResponse response = new AccountingVoucherResponse();
        response.id = voucher.getId();
        response.voucherNo = voucher.getVoucherNo();
        response.voucherDate = voucher.getVoucherDate();
        response.amount = voucher.getAmount().getValue();
        response.currencyCode = voucher.getCurrencyCode();
        response.sourceEventType = voucher.getSourceEventType();
        response.sourceEventId = voucher.getSourceEventId();
        response.status = voucher.getStatus();
        return response;
    }
}
