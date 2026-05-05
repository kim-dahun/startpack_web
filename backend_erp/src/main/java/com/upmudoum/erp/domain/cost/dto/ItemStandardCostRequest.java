package com.upmudoum.erp.domain.cost.dto;

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
public class ItemStandardCostRequest {

    @NotNull
    private Long itemId;

    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal standardCost;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currencyCode = "KRW";

    @NotNull
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}
