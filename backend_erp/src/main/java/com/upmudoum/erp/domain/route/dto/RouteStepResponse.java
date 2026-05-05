package com.upmudoum.erp.domain.route.dto;

import com.upmudoum.erp.domain.route.entity.RouteStep;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RouteStepResponse {

    private Long id;
    private Long routeId;
    private Integer sequenceNo;
    private Long processId;
    private String processCode;
    private Long defaultEquipmentId;
    private String defaultEquipmentCode;
    private Integer standardLeadTimeMinutes;
    private String description;
    private boolean enabled;

    public static RouteStepResponse from(RouteStep step) {
        RouteStepResponse response = new RouteStepResponse();
        response.id = step.getId();
        response.routeId = step.getRoute().getId();
        response.sequenceNo = step.getSequenceNo();
        response.processId = step.getProcess().getId();
        response.processCode = step.getProcess().getCode();
        response.defaultEquipmentId = step.getDefaultEquipment() == null ? null : step.getDefaultEquipment().getId();
        response.defaultEquipmentCode = step.getDefaultEquipment() == null ? null : step.getDefaultEquipment().getCode();
        response.standardLeadTimeMinutes = step.getStandardLeadTimeMinutes();
        response.description = step.getDescription();
        response.enabled = step.isEnabled();
        return response;
    }
}
