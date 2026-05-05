package com.upmudoum.erp.domain.equipment.dto;

import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.equipment.vo.EquipmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EquipmentResponse {

    private Long id;
    private String code;
    private String name;
    private String equipmentType;
    private Long warehouseId;
    private String warehouseCode;
    private String location;
    private EquipmentStatus status;
    private boolean enabled;

    public static EquipmentResponse from(Equipment equipment) {
        EquipmentResponse response = new EquipmentResponse();
        response.id = equipment.getId();
        response.code = equipment.getCode();
        response.name = equipment.getName();
        response.equipmentType = equipment.getEquipmentType();
        response.warehouseId = equipment.getWarehouse() == null ? null : equipment.getWarehouse().getId();
        response.warehouseCode = equipment.getWarehouse() == null ? null : equipment.getWarehouse().getCode().getValue();
        response.location = equipment.getLocation();
        response.status = equipment.getStatus();
        response.enabled = equipment.isEnabled();
        return response;
    }
}
