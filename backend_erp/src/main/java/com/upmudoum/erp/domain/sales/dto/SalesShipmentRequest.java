package com.upmudoum.erp.domain.sales.dto;

import com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesShipmentRequest {

    @NotNull
    private Long partnerId;

    @NotNull
    private Long itemId;

    @NotNull
    private Long warehouseId;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal unitPrice;

    @NotNull
    private LocalDate salesDate;

    @Valid
    private List<LotDeductionRequest> lotSelections;
}
