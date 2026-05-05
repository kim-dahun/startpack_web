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
        name = "watchlist_group",
        uniqueConstraints = @UniqueConstraint(name = "uk_watchlist_group_user_name", columnNames = {"user_id", "group_name"})
)
public class WatchlistGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(nullable = false)
    private Instant createdAt;
}
