package com.upmudoum.erp.domain.inventory.entity;

import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.warehouse.entity.Warehouse;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_inventory_balances", uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_balance_item_warehouse", columnNames = {"item_id", "warehouse_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryBalance {

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
    private Quantity quantity = Quantity.zero();

    public InventoryBalance(Item item, Warehouse warehouse) {
        this.item = item;
        this.warehouse = warehouse;
    }

    public void add(Quantity amount) {
        this.quantity = this.quantity.add(amount);
    }
}
