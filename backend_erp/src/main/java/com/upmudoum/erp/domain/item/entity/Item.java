package com.upmudoum.erp.domain.item.entity;

import com.upmudoum.erp.domain.item.vo.ItemCode;
import com.upmudoum.erp.domain.item.vo.ItemType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ItemCode code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemType itemType = ItemType.RAW_MATERIAL;

    @Column(nullable = false)
    private boolean active = true;

    public Item(String code, String name, String unit) {
        this(code, name, unit, ItemType.RAW_MATERIAL);
    }

    public Item(String code, String name, String unit, ItemType itemType) {
        this.code = ItemCode.of(code);
        this.name = name;
        this.unit = unit;
        this.itemType = itemType;
    }

    public void update(String name, String unit, ItemType itemType, boolean active) {
        this.name = name;
        this.unit = unit;
        this.itemType = itemType;
        this.active = active;
    }

}
