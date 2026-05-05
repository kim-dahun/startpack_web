package com.upmudoum.trade.domain.marketdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "realtime_reconnect_history")
public class RealtimeReconnectHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant attemptedAt;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false)
    private int subscriptionCount;

    @Column(length = 1000)
    private String failureReason;
}
