package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.production.entity.ProductionResultStep;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductionResultStepResponse {

    private Long id;
    private Long productionResultId;
    private Long productionOrderStepId;
    private Integer sequenceNo;
    private Long processId;
    private String processCode;
    private Long equipmentId;
    private String equipmentCode;
    private LocalDateTime workStartedAt;
    private LocalDateTime workEndedAt;

    public static ProductionResultStepResponse from(ProductionResultStep step) {
        ProductionResultStepResponse response = new ProductionResultStepResponse();
        response.id = step.getId();
        response.productionResultId = step.getProductionResult().getId();
        response.productionOrderStepId = step.getProductionOrderStep() == null ? null : step.getProductionOrderStep().getId();
        response.sequenceNo = step.getSequenceNo();
        response.processId = step.getProcess().getId();
        response.processCode = step.getProcess().getCode();
        response.equipmentId = step.getEquipment() == null ? null : step.getEquipment().getId();
        response.equipmentCode = step.getEquipment() == null ? null : step.getEquipment().getCode();
        response.workStartedAt = step.getWorkStartedAt();
        response.workEndedAt = step.getWorkEndedAt();
        return response;
    }
}
