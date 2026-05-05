package com.upmudoum.erp.domain.inventory.entity;

import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_inventory_transfers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String transferNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_warehouse_id", nullable = false)
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_warehouse_id", nullable = false)
    private Warehouse toWarehouse;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false, precision = 19, scale = 6))
    private Quantity quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "out_movement_id")
    private InventoryMovement outMovement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "in_movement_id")
    private InventoryMovement inMovement;

    @Column(length = 200)
    private String memo;

    @Column(nullable = false)
    private LocalDateTime transferredAt;

    public InventoryTransfer(String transferNo, Item item, Warehouse fromWarehouse, Warehouse toWarehouse,
                             BigDecimal quantity, String memo, LocalDateTime transferredAt) {
        this.transferNo = transferNo;
        this.item = item;
        this.fromWarehouse = fromWarehouse;
        this.toWarehouse = toWarehouse;
        this.quantity = Quantity.of(quantity);
        this.memo = memo;
        this.transferredAt = transferredAt;
    }

    public void linkMovements(InventoryMovement outMovement, InventoryMovement inMovement) {
        this.outMovement = outMovement;
        this.inMovement = inMovement;
    }
}
