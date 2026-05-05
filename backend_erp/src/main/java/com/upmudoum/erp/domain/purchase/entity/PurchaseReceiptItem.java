package com.upmudoum.erp.domain.purchase.entity;

import com.upmudoum.erp.domain.accounting.vo.MoneyAmount;
import com.upmudoum.erp.domain.accounting.vo.UnitPrice;
import com.upmudoum.erp.domain.inventory.entity.InventoryMovement;
import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.item.entity.Item;
import com.upmudoum.erp.domain.lot.entity.Lot;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_purchase_receipt_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_receipt_id", nullable = false)
    private PurchaseReceipt purchaseReceipt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false, precision = 19, scale = 6))
    private Quantity quantity;

    @Embedded
    private UnitPrice unitPrice;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "supply_amount", nullable = false, precision = 19, scale = 2))
    private MoneyAmount supplyAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_movement_id")
    private InventoryMovement inventoryMovement;

    public PurchaseReceiptItem(PurchaseReceipt purchaseReceipt, Item item, Warehouse warehouse, Lot lot,
                               BigDecimal quantity, BigDecimal unitPrice, BigDecimal supplyAmount,
                               InventoryMovement inventoryMovement) {
        this.purchaseReceipt = purchaseReceipt;
        this.item = item;
        this.warehouse = warehouse;
        this.lot = lot;
        this.quantity = Quantity.of(quantity);
        this.unitPrice = UnitPrice.of(unitPrice);
        this.supplyAmount = MoneyAmount.of(supplyAmount);
        this.inventoryMovement = inventoryMovement;
    }
}
