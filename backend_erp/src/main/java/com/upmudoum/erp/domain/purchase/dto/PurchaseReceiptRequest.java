package com.upmudoum.erp.domain.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseReceiptRequest {

    @NotNull
    private Long partnerId;

    @NotNull
    private Long itemId;

    @NotNull
    private Long warehouseId;

    @Size(max = 80)
    private String lotNo;

    @NotNull
    @DecimalMin(value = "0.000001")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal unitPrice;

    @NotNull
    private LocalDate purchaseDate;
}
