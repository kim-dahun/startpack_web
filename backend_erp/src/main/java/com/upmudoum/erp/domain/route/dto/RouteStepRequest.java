package com.upmudoum.erp.domain.route.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteStepRequest {

    @NotNull
    @Min(1)
    private Integer sequenceNo;

    @NotNull
    private Long processId;

    private Long defaultEquipmentId;

    @Min(0)
    private Integer standardLeadTimeMinutes;

    @Size(max = 200)
    private String description;

    private boolean enabled = true;
}
