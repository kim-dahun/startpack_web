package com.upmudoum.erp.domain.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LotDeductionRequest {

    @NotNull
    private Long lotId;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal quantity;

    public LotDeductionRequest(Long lotId, BigDecimal quantity) {
        this.lotId = lotId;
        this.quantity = quantity;
    }

    public LotDeductionRequest() {
    }
}
