package com.upmudoum.erp.domain.item.entity;

import jakarta.persistence.Column;
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
@Table(name = "erp_item_categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_item_category_code", columnNames = "code")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private ItemCategory parentCategory;

    @Column(nullable = false)
    private int depth;

    @Column(nullable = false)
    private boolean active = true;

    public ItemCategory(String code, String name, ItemCategory parentCategory) {
        this.code = code;
        this.name = name;
        this.parentCategory = parentCategory;
        this.depth = parentCategory == null ? 1 : parentCategory.getDepth() + 1;
    }

    public void update(String name, boolean active) {
        this.name = name;
        this.active = active;
    }
}
