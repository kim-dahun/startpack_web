package com.upmudoum.erp.domain.bom.entity;

import com.upmudoum.erp.domain.item.entity.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_boms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_item_id", nullable = false)
    private Item parentItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_bom_version_id")
    private BomVersion defaultBomVersion;

    @Column(nullable = false)
    private boolean enabled = true;

    public Bom(Item parentItem) {
        if (!parentItem.getItemType().isProducible()) {
            throw new IllegalArgumentException("BOM parent item must be producible");
        }
        this.parentItem = parentItem;
    }

    public void setDefaultVersion(BomVersion bomVersion) {
        if (!bomVersion.getBom().equals(this)) {
            throw new IllegalArgumentException("Default BOM version must belong to this BOM");
        }
        this.defaultBomVersion = bomVersion;
    }
}
