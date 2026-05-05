package com.upmudoum.erp.domain.bom.dto;

import com.upmudoum.erp.domain.bom.entity.BomComponent;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BomComponentResponse {

    private Long id;
    private Long bomVersionId;
    private Long componentItemId;
    private String componentItemCode;
    private BigDecimal requiredQuantity;
    private BigDecimal lossRate;

    public static BomComponentResponse from(BomComponent component) {
        BomComponentResponse response = new BomComponentResponse();
        response.id = component.getId();
        response.bomVersionId = component.getBomVersion().getId();
        response.componentItemId = component.getComponentItem().getId();
        response.componentItemCode = component.getComponentItem().getCode().getValue();
        response.requiredQuantity = component.getRequiredQuantity().getValue();
        response.lossRate = component.getLossRate();
        return response;
    }
}
