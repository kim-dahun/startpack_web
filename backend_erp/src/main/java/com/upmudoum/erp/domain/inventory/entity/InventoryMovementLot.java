package com.upmudoum.erp.domain.inventory.entity;

import com.upmudoum.erp.domain.inventory.vo.Quantity;
import com.upmudoum.erp.domain.lot.entity.Lot;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_inventory_movement_lots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryMovementLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movement_id", nullable = false)
    private InventoryMovement movement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false, precision = 19, scale = 6))
    private Quantity quantity;

    public InventoryMovementLot(InventoryMovement movement, Lot lot, Quantity quantity) {
        this.movement = movement;
        this.lot = lot;
        this.quantity = quantity;
    }
}
