package com.upmudoum.erp.domain.cost.dto;

import com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualCostReversalRequest {

    @NotNull
    private ActualCostReferenceType originalReferenceType;

    @NotNull
    private Long originalReferenceId;
}
