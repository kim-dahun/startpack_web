package com.upmudoum.erp.domain.bom.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BomComponentRequest {

    @NotNull
    private Long componentItemId;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal requiredQuantity;

    @DecimalMin(value = "0.000000")
    private BigDecimal lossRate = BigDecimal.ZERO;
}
