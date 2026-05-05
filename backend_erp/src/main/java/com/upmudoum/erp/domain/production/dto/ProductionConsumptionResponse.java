package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.production.entity.ProductionConsumption;
import com.upmudoum.erp.domain.production.vo.ProductionConsumptionAdjustType;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductionConsumptionResponse {

    private Long id;
    private Long productionResultId;
    private Long itemId;
    private String itemCode;
    private BigDecimal plannedQuantity;
    private BigDecimal actualQuantity;
    private ProductionConsumptionAdjustType adjustType;

    public static ProductionConsumptionResponse from(ProductionConsumption consumption) {
        ProductionConsumptionResponse response = new ProductionConsumptionResponse();
        response.id = consumption.getId();
        response.productionResultId = consumption.getProductionResult().getId();
        response.itemId = consumption.getItem().getId();
        response.itemCode = consumption.getItem().getCode().getValue();
        response.plannedQuantity = consumption.getPlannedQuantity().getValue();
        response.actualQuantity = consumption.getActualQuantity().getValue();
        response.adjustType = consumption.getAdjustType();
        return response;
    }
}
