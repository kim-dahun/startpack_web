package com.upmudoum.erp.domain.item.entity;

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
@Table(name = "erp_item_category_assignments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_item_category_assignment", columnNames = {"item_id", "category_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCategoryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ItemCategory category;

    public ItemCategoryAssignment(Item item, ItemCategory category) {
        this.item = item;
        this.category = category;
    }
}
