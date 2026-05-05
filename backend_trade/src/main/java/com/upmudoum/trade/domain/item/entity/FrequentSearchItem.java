package com.upmudoum.trade.domain.item.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "frequent_search_item",
        uniqueConstraints = @UniqueConstraint(name = "uk_frequent_search_item_user_item", columnNames = {"user_id", "item_code"})
)
public class FrequentSearchItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, length = 30)
    private String itemCode;

    @Column(nullable = false, length = 100)
    private String itemName;

    @Column(nullable = false, length = 30)
    private String marketCode;

    @Column(nullable = false)
    private long searchCount;

    @Column(nullable = false)
    private Instant lastSearchedAt;
}
