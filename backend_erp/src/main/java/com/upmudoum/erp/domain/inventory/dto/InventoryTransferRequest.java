package com.upmudoum.erp.domain.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryTransferRequest {

    @NotBlank
    @Size(max = 50)
    private String transferNo;

    @NotNull
    private Long itemId;

    @NotNull
    private Long fromWarehouseId;

    @NotNull
    private Long toWarehouseId;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal quantity;

    @Size(max = 80)
    private String toLotNo;

    @Valid
    private List<LotDeductionRequest> lotSelections;

    @Size(max = 200)
    private String memo;
}
