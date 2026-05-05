package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest;
import com.upmudoum.erp.domain.production.vo.ProductionConsumptionAdjustType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionConsumptionAdjustmentRequest {

    @NotNull
    private Long itemId;

    @NotNull
    @DecimalMin(value = "0.000000")
    private BigDecimal actualQuantity;

    @NotNull
    private ProductionConsumptionAdjustType adjustType;

    @Valid
    private List<LotDeductionRequest> lotSelections;

    public ProductionConsumptionAdjustmentRequest(Long itemId, BigDecimal actualQuantity,
                                                  ProductionConsumptionAdjustType adjustType,
                                                  List<LotDeductionRequest> lotSelections) {
        this.itemId = itemId;
        this.actualQuantity = actualQuantity;
        this.adjustType = adjustType;
        this.lotSelections = lotSelections;
    }

    public ProductionConsumptionAdjustmentRequest() {
    }
}
