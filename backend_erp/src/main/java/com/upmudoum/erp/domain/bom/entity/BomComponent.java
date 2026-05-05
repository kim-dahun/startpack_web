package com.upmudoum.erp.domain.bom.entity;

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
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_bom_components")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BomComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bom_version_id", nullable = false)
    private BomVersion bomVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "component_item_id", nullable = false)
    private Item componentItem;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "required_qty", nullable = false, precision = 19, scale = 6))
    private Quantity requiredQuantity;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lossRate = BigDecimal.ZERO;

    public BomComponent(BomVersion bomVersion, Item componentItem, BigDecimal requiredQuantity, BigDecimal lossRate) {
        this.bomVersion = bomVersion;
        this.componentItem = componentItem;
        this.requiredQuantity = Quantity.of(requiredQuantity);
        this.lossRate = lossRate == null ? BigDecimal.ZERO : lossRate;
    }
}
