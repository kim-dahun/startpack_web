package com.upmudoum.erp.domain.lot.entity;

import com.upmudoum.erp.domain.item.entity.Item;
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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_lots", uniqueConstraints = {
        @UniqueConstraint(name = "uk_lot_item_lot_no", columnNames = {"item_id", "lot_no"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "lot_no", length = 80)
    private String lotNo;

    private LocalDate manufacturedDate;

    private LocalDate expiredDate;

    @Column(nullable = false)
    private boolean enabled = true;

    public Lot(Item item, String lotNo, LocalDate manufacturedDate, LocalDate expiredDate) {
        this.item = item;
        this.lotNo = lotNo;
        this.manufacturedDate = manufacturedDate;
        this.expiredDate = expiredDate;
    }
}
