package com.upmudoum.erp.domain.cost.entity;

import com.upmudoum.erp.domain.accounting.vo.UnitPrice;
import com.upmudoum.erp.domain.item.entity.Item;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_item_standard_costs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemStandardCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Embedded
    private UnitPrice standardCost;

    @Column(nullable = false, length = 3)
    private String currencyCode;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Column(nullable = false)
    private boolean enabled = true;

    public ItemStandardCost(Item item, BigDecimal standardCost, String currencyCode, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.item = item;
        this.standardCost = UnitPrice.of(standardCost);
        this.currencyCode = currencyCode;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }
}
