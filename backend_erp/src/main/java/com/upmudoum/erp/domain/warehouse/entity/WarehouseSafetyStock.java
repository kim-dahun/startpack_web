package com.upmudoum.erp.domain.warehouse.entity;

import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_warehouse_safety_stocks", uniqueConstraints = {
        @UniqueConstraint(name = "uk_warehouse_safety_stock", columnNames = {"item_id", "warehouse_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WarehouseSafetyStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "safety_quantity", nullable = false, precision = 19, scale = 6))
    private Quantity safetyQuantity;

    @Column(nullable = false)
    private boolean active = true;

    public WarehouseSafetyStock(Item item, Warehouse warehouse, BigDecimal safetyQuantity) {
        this.item = item;
        this.warehouse = warehouse;
        this.safetyQuantity = Quantity.of(safetyQuantity);
    }

    public void update(BigDecimal safetyQuantity, boolean active) {
        this.safetyQuantity = Quantity.of(safetyQuantity);
        this.active = active;
    }
}
