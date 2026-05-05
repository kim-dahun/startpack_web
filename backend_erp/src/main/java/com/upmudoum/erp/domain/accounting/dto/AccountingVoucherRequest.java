package com.upmudoum.erp.domain.accounting.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingVoucherRequest {

    @NotBlank
    @Size(max = 50)
    private String voucherNo;

    @NotNull
    private LocalDate voucherDate;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal amount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currencyCode = "KRW";

    @Size(max = 100)
    private String sourceEventType;

    @Size(max = 100)
    private String sourceEventId;

    private boolean postImmediately;
}
