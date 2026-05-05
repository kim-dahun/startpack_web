package com.upmudoum.erp.domain.production.entity;

import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.production.vo.ProductionConsumptionAdjustType;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_production_consumptions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductionConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "production_result_id", nullable = false)
    private ProductionResult productionResult;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "planned_qty", nullable = false, precision = 19, scale = 6))
    private Quantity plannedQuantity;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "actual_qty", nullable = false, precision = 19, scale = 6))
    private Quantity actualQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductionConsumptionAdjustType adjustType;

    public ProductionConsumption(ProductionResult productionResult, Item item, BigDecimal plannedQuantity,
                                 BigDecimal actualQuantity, ProductionConsumptionAdjustType adjustType) {
        this.productionResult = productionResult;
        this.item = item;
        this.plannedQuantity = Quantity.of(plannedQuantity);
        this.actualQuantity = Quantity.of(actualQuantity);
        this.adjustType = adjustType;
    }
}
