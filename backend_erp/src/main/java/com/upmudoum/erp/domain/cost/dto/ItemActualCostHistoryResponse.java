package com.upmudoum.erp.domain.cost.dto;

import com.upmudoum.erp.domain.cost.entity.ItemActualCostHistory;
import com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemActualCostHistoryResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private ActualCostReferenceType referenceType;
    private Long referenceId;
    private BigDecimal unitCost;
    private BigDecimal quantity;
    private LocalDateTime appliedAt;

    public static ItemActualCostHistoryResponse from(ItemActualCostHistory history) {
        ItemActualCostHistoryResponse response = new ItemActualCostHistoryResponse();
        response.id = history.getId();
        response.itemId = history.getItem().getId();
        response.itemCode = history.getItem().getCode().getValue();
        response.referenceType = history.getReferenceType();
        response.referenceId = history.getReferenceId();
        response.unitCost = history.getUnitCost().getValue();
        response.quantity = history.getQuantity().getValue();
        response.appliedAt = history.getAppliedAt();
        return response;
    }
}
