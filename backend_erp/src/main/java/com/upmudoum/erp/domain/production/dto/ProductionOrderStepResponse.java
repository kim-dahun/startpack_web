package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.production.entity.ProductionOrderStep;
import com.upmudoum.erp.domain.production.vo.ProductionStepStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductionOrderStepResponse {

    private Long id;
    private Long productionOrderId;
    private Long routeStepId;
    private Integer sequenceNo;
    private Long processId;
    private String processCode;
    private Long plannedEquipmentId;
    private String plannedEquipmentCode;
    private ProductionStepStatus status;
    private LocalDateTime plannedStartAt;
    private LocalDateTime plannedEndAt;

    public static ProductionOrderStepResponse from(ProductionOrderStep step) {
        ProductionOrderStepResponse response = new ProductionOrderStepResponse();
        response.id = step.getId();
        response.productionOrderId = step.getProductionOrder().getId();
        response.routeStepId = step.getRouteStep() == null ? null : step.getRouteStep().getId();
        response.sequenceNo = step.getSequenceNo();
        response.processId = step.getProcess().getId();
        response.processCode = step.getProcess().getCode();
        response.plannedEquipmentId = step.getPlannedEquipment() == null ? null : step.getPlannedEquipment().getId();
        response.plannedEquipmentCode = step.getPlannedEquipment() == null ? null : step.getPlannedEquipment().getCode();
        response.status = step.getStatus();
        response.plannedStartAt = step.getPlannedStartAt();
        response.plannedEndAt = step.getPlannedEndAt();
        return response;
    }
}
