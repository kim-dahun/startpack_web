package com.upmudoum.erp.domain.production.entity;

import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import com.upmudoum.erp.domain.production.vo.ProductionResultStatus;
import com.upmudoum.erp.domain.route.entity.Route;
import com.upmudoum.erp.domain.route.entity.RouteStep;
import jakarta.persistence.AttributeOverride;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_production_results")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_step_id")
    private RouteStep routeStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private ErpProcess process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "good_quantity", nullable = false, precision = 19, scale = 6))
    private Quantity goodQuantity;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "defect_quantity", nullable = false, precision = 19, scale = 6))
    private Quantity defectQuantity;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    private LocalDateTime workStartedAt;

    private LocalDateTime workEndedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductionResultStatus status = ProductionResultStatus.RECORDED;

    public ProductionResult(ProductionOrder productionOrder, BigDecimal goodQuantity, BigDecimal defectQuantity,
                            LocalDateTime completedAt) {
        this(productionOrder, null, null, null, null, goodQuantity, defectQuantity, completedAt, null, null);
    }

    public ProductionResult(ProductionOrder productionOrder, Route route, RouteStep routeStep, ErpProcess process,
                            Equipment equipment, BigDecimal goodQuantity, BigDecimal defectQuantity,
                            LocalDateTime completedAt, LocalDateTime workStartedAt, LocalDateTime workEndedAt) {
        this.productionOrder = productionOrder;
        this.route = route;
        this.routeStep = routeStep;
        this.process = process;
        this.equipment = equipment;
        this.goodQuantity = Quantity.of(goodQuantity);
        this.defectQuantity = Quantity.of(defectQuantity);
        this.completedAt = completedAt;
        this.workStartedAt = workStartedAt;
        this.workEndedAt = workEndedAt;
    }

    public void cancel() {
        this.status = ProductionResultStatus.CANCELED;
    }
}
