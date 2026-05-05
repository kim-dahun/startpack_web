package com.upmudoum.erp.domain.equipment.dto;

import com.upmudoum.erp.domain.equipment.entity.EquipmentProcess;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EquipmentProcessResponse {

    private Long id;
    private Long equipmentId;
    private String equipmentCode;
    private Long processId;
    private String processCode;
    private String processName;
    private boolean enabled;

    public static EquipmentProcessResponse from(EquipmentProcess mapping) {
        EquipmentProcessResponse response = new EquipmentProcessResponse();
        response.id = mapping.getId();
        response.equipmentId = mapping.getEquipment().getId();
        response.equipmentCode = mapping.getEquipment().getCode();
        response.processId = mapping.getProcess().getId();
        response.processCode = mapping.getProcess().getCode();
        response.processName = mapping.getProcess().getName();
        response.enabled = mapping.isEnabled();
        return response;
    }
}
