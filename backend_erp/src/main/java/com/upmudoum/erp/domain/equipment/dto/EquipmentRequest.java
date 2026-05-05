package com.upmudoum.erp.domain.equipment.dto;

import com.upmudoum.erp.domain.equipment.vo.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentRequest {

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 30)
    private String equipmentType;

    private Long warehouseId;

    @Size(max = 200)
    private String location;

    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    private boolean enabled = true;
}
