package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.production.vo.ProductionStepStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionOrderStepRequest {

    private Long plannedEquipmentId;

    private ProductionStepStatus status;

    private LocalDateTime plannedStartAt;

    private LocalDateTime plannedEndAt;
}
