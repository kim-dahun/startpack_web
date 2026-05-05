package com.upmudoum.trade.domain.watchlist.entity;

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
        name = "watchlist_item",
        uniqueConstraints = @UniqueConstraint(name = "uk_watchlist_user_item", columnNames = {"user_id", "item_code"})
)
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "item_code", nullable = false, length = 30)
    private String itemCode;

    @Column(nullable = false, length = 200)
    private String itemName;

    @Column(name = "group_id")
    private Long groupId;

    @Column(length = 1000)
    private String memo;

    @Column(length = 1000)
    private String tags;

    @Column(nullable = false)
    private Instant createdAt;
}
