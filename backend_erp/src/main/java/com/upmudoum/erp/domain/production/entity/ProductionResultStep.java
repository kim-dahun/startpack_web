package com.upmudoum.erp.domain.production.entity;

import com.upmudoum.erp.domain.equipment.entity.Equipment;
import com.upmudoum.erp.domain.process.entity.ErpProcess;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "erp_production_result_steps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductionResultStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "production_result_id", nullable = false)
    private ProductionResult productionResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_order_step_id")
    private ProductionOrderStep productionOrderStep;

    @Column(nullable = false)
    private Integer sequenceNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_id", nullable = false)
    private ErpProcess process;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private LocalDateTime workStartedAt;

    private LocalDateTime workEndedAt;

    public ProductionResultStep(ProductionResult productionResult, ProductionOrderStep productionOrderStep,
                                Integer sequenceNo, ErpProcess process, Equipment equipment,
                                LocalDateTime workStartedAt, LocalDateTime workEndedAt) {
        this.productionResult = productionResult;
        this.productionOrderStep = productionOrderStep;
        this.sequenceNo = sequenceNo;
        this.process = process;
        this.equipment = equipment;
        this.workStartedAt = workStartedAt;
        this.workEndedAt = workEndedAt;
    }
}
