package com.upmudoum.erp.domain.production.entity;

import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import com.upmudoum.erp.domain.production.vo.ProductionStepStatus;
import com.upmudoum.erp.domain.route.entity.RouteStep;
import jakarta.persistence.Column;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_production_order_steps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductionOrderStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_step_id")
    private RouteStep routeStep;

    @Column(nullable = false)
    private Integer sequenceNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    private ErpProcess process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planned_equipment_id")
    private Equipment plannedEquipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductionStepStatus status = ProductionStepStatus.PLANNED;

    private LocalDateTime plannedStartAt;

    private LocalDateTime plannedEndAt;

    public ProductionOrderStep(ProductionOrder productionOrder, RouteStep routeStep, Integer sequenceNo,
                               ErpProcess process, Equipment plannedEquipment) {
        this.productionOrder = productionOrder;
        this.routeStep = routeStep;
        this.sequenceNo = sequenceNo;
        this.process = process;
        this.plannedEquipment = plannedEquipment;
    }

    public void update(Equipment plannedEquipment, ProductionStepStatus status,
                       LocalDateTime plannedStartAt, LocalDateTime plannedEndAt) {
        this.plannedEquipment = plannedEquipment;
        this.status = status == null ? this.status : status;
        this.plannedStartAt = plannedStartAt;
        this.plannedEndAt = plannedEndAt;
    }
}
