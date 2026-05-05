package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.production.entity.ProductionResult;
import com.upmudoum.erp.domain.production.vo.ProductionResultStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductionResultResponse {

    private Long id;
    private Long productionOrderId;
    private String orderNo;
    private Long routeId;
    private String routeCode;
    private Long routeStepId;
    private Long processId;
    private String processCode;
    private Long equipmentId;
    private String equipmentCode;
    private BigDecimal goodQuantity;
    private BigDecimal defectQuantity;
    private LocalDateTime completedAt;
    private LocalDateTime workStartedAt;
    private LocalDateTime workEndedAt;
    private ProductionResultStatus status;

    public static ProductionResultResponse from(ProductionResult result) {
        ProductionResultResponse response = new ProductionResultResponse();
        response.id = result.getId();
        response.productionOrderId = result.getProductionOrder().getId();
        response.orderNo = result.getProductionOrder().getOrderNo();
        response.routeId = result.getRoute() == null ? null : result.getRoute().getId();
        response.routeCode = result.getRoute() == null ? null : result.getRoute().getCode();
        response.routeStepId = result.getRouteStep() == null ? null : result.getRouteStep().getId();
        response.processId = result.getProcess() == null ? null : result.getProcess().getId();
        response.processCode = result.getProcess() == null ? null : result.getProcess().getCode();
        response.equipmentId = result.getEquipment() == null ? null : result.getEquipment().getId();
        response.equipmentCode = result.getEquipment() == null ? null : result.getEquipment().getCode();
        response.goodQuantity = result.getGoodQuantity().getValue();
        response.defectQuantity = result.getDefectQuantity().getValue();
        response.completedAt = result.getCompletedAt();
        response.workStartedAt = result.getWorkStartedAt();
        response.workEndedAt = result.getWorkEndedAt();
        response.status = result.getStatus();
        return response;
    }
}
