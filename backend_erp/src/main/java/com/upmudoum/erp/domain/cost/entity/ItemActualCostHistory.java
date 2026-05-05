package com.upmudoum.erp.domain.cost.entity;

import com.upmudoum.erp.domain.accounting.vo.UnitPrice;
import com.upmudoum.erp.domain.cost.vo.ActualCostReferenceType;
import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
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
@Table(name = "erp_item_actual_cost_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemActualCostHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActualCostReferenceType referenceType;

    @Column(nullable = false)
    private Long referenceId;

    @Embedded
    private UnitPrice unitCost;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false, precision = 19, scale = 6))
    private Quantity quantity;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    public ItemActualCostHistory(Item item, ActualCostReferenceType referenceType, Long referenceId,
                                 BigDecimal unitCost, BigDecimal quantity, LocalDateTime appliedAt) {
        this.item = item;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.unitCost = UnitPrice.of(unitCost);
        this.quantity = Quantity.of(quantity);
        this.appliedAt = appliedAt;
    }
}
