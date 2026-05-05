package com.upmudoum.trade.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account_snapshot")
public class AccountSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String accountNo;

    @Column(nullable = false, length = 100)
    private String accountName;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAssetAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashAmount;

    @Column(nullable = false)
    private Instant capturedAt;
}
