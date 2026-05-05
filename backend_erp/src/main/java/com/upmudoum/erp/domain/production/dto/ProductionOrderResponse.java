package com.upmudoum.erp.domain.production.dto;

import com.upmudoum.erp.domain.production.entity.ProductionOrder;
import com.upmudoum.erp.domain.production.vo.ProductionOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductionOrderResponse {

    private Long id;
    private String orderNo;
    private Long itemId;
    private String itemCode;
    private Long bomVersionId;
    private Long routeId;
    private String routeCode;
    private Long plannedProcessId;
    private String plannedProcessCode;
    private Long plannedEquipmentId;
    private String plannedEquipmentCode;
    private BigDecimal plannedQuantity;
    private LocalDate dueDate;
    private ProductionOrderStatus status;

    public static ProductionOrderResponse from(ProductionOrder order) {
        ProductionOrderResponse response = new ProductionOrderResponse();
        response.id = order.getId();
        response.orderNo = order.getOrderNo();
        response.itemId = order.getItem().getId();
        response.itemCode = order.getItem().getCode().getValue();
        response.bomVersionId = order.getBomVersion().getId();
        response.routeId = order.getRoute() == null ? null : order.getRoute().getId();
        response.routeCode = order.getRoute() == null ? null : order.getRoute().getCode();
        response.plannedProcessId = order.getPlannedProcess() == null ? null : order.getPlannedProcess().getId();
        response.plannedProcessCode = order.getPlannedProcess() == null ? null : order.getPlannedProcess().getCode();
        response.plannedEquipmentId = order.getPlannedEquipment() == null ? null : order.getPlannedEquipment().getId();
        response.plannedEquipmentCode = order.getPlannedEquipment() == null ? null : order.getPlannedEquipment().getCode();
        response.plannedQuantity = order.getPlannedQuantity().getValue();
        response.dueDate = order.getDueDate();
        response.status = order.getStatus();
        return response;
    }
}
