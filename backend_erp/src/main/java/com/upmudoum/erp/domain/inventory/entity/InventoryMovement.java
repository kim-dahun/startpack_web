package com.upmudoum.erp.domain.inventory.entity;

import com.upmudoum.erp.domain.accounting.vo.MoneyAmount;
import com.upmudoum.erp.domain.accounting.vo.UnitPrice;
import com.upmudoum.erp.domain.inventory.vo.InventoryMovementType;
import com.upmudoum.erp.domain.inventory.vo.InventoryReferenceType;
import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Embedded;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_inventory_movements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InventoryMovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InventoryReferenceType referenceType = InventoryReferenceType.MANUAL;

    private Long referenceId;

    @Embedded
    private Quantity quantity;

    @Embedded
    private UnitPrice unitCost;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "supply_amount", nullable = false, precision = 19, scale = 2))
    private MoneyAmount supplyAmount;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "balance_after", nullable = false, precision = 19, scale = 6))
    private Quantity balanceAfter;

    @Column(length = 200)
    private String memo;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    public InventoryMovement(Item item, Warehouse warehouse, InventoryMovementType movementType, Quantity quantity,
                             Quantity balanceAfter, String memo, LocalDateTime occurredAt) {
        this(item, warehouse, movementType, InventoryReferenceType.MANUAL, null, quantity,
                BigDecimal.ZERO, BigDecimal.ZERO, balanceAfter, memo, occurredAt);
    }

    public InventoryMovement(Item item, Warehouse warehouse, InventoryMovementType movementType,
                             InventoryReferenceType referenceType, Long referenceId, Quantity quantity,
                             BigDecimal unitCost, BigDecimal supplyAmount, Quantity balanceAfter,
                             String memo, LocalDateTime occurredAt) {
        this.item = item;
        this.warehouse = warehouse;
        this.movementType = movementType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.quantity = quantity;
        this.unitCost = UnitPrice.of(unitCost);
        this.supplyAmount = MoneyAmount.of(supplyAmount);
        this.balanceAfter = balanceAfter;
        this.memo = memo;
        this.occurredAt = occurredAt;
    }

}
