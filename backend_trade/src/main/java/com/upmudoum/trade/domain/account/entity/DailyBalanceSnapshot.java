package com.upmudoum.trade.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "daily_balance_snapshot")
public class DailyBalanceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String accountNo;

    @Column(nullable = false)
    private LocalDate baseDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAssetAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal profitLossAmount;

    @Column(nullable = false)
    private Instant capturedAt;
}
