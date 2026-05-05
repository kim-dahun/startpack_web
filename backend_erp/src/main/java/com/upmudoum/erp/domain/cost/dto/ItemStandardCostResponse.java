package com.upmudoum.erp.domain.cost.dto;

import com.upmudoum.erp.domain.cost.entity.ItemStandardCost;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemStandardCostResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private BigDecimal standardCost;
    private String currencyCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private boolean enabled;

    public static ItemStandardCostResponse from(ItemStandardCost standardCost) {
        ItemStandardCostResponse response = new ItemStandardCostResponse();
        response.id = standardCost.getId();
        response.itemId = standardCost.getItem().getId();
        response.itemCode = standardCost.getItem().getCode().getValue();
        response.standardCost = standardCost.getStandardCost().getValue();
        response.currencyCode = standardCost.getCurrencyCode();
        response.effectiveFrom = standardCost.getEffectiveFrom();
        response.effectiveTo = standardCost.getEffectiveTo();
        response.enabled = standardCost.isEnabled();
        return response;
    }
}
