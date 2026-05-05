package com.upmudoum.erp.domain.production.entity;

import com.upmudoum.erp.domain.bom.entity.BomVersion;
import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import com.upmudoum.erp.domain.production.vo.ProductionOrderStatus;
import com.upmudoum.erp.domain.route.entity.Route;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_production_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bom_version_id", nullable = false)
    private BomVersion bomVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planned_process_id")
    private ErpProcess plannedProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planned_equipment_id")
    private Equipment plannedEquipment;

    @Embedded
    private Quantity plannedQuantity;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductionOrderStatus status = ProductionOrderStatus.PLANNED;

    public ProductionOrder(String orderNo, Item item, BigDecimal plannedQuantity, LocalDate dueDate) {
        this(orderNo, item, null, plannedQuantity, dueDate);
    }

    public ProductionOrder(String orderNo, Item item, BomVersion bomVersion, BigDecimal plannedQuantity, LocalDate dueDate) {
        this(orderNo, item, bomVersion, null, null, null, plannedQuantity, dueDate);
    }

    public ProductionOrder(String orderNo, Item item, BomVersion bomVersion, Route route, ErpProcess plannedProcess,
                           Equipment plannedEquipment, BigDecimal plannedQuantity, LocalDate dueDate) {
        if (!item.getItemType().isProducible()) {
            throw new IllegalArgumentException("Only semi-finished or finished goods can be produced");
        }
        this.orderNo = orderNo;
        this.item = item;
        this.bomVersion = bomVersion;
        this.route = route;
        this.plannedProcess = plannedProcess;
        this.plannedEquipment = plannedEquipment;
        this.plannedQuantity = Quantity.of(plannedQuantity);
        this.dueDate = dueDate;
    }

    public void complete() {
        this.status = ProductionOrderStatus.COMPLETED;
    }
}
