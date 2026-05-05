package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.inventory.dto.LotDeductionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionResultCancelRequest {

    @NotNull
    private Long warehouseId;

    @Valid
    private List<LotDeductionRequest> finishedLotSelections;

    @Size(max = 200)
    private String memo;
}
