package com.upmudoum.erp.domain.equipment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentProcessRequest {

    @NotNull
    private Long processId;
}
