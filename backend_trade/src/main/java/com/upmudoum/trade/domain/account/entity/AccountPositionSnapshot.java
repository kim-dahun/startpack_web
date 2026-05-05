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
@Table(name = "account_position_snapshot")
public class AccountPositionSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String accountNo;

    @Column(nullable = false, length = 30)
    private String itemCode;

    @Column(nullable = false, length = 200)
    private String itemName;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private long orderableQuantity;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal averagePrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal evaluationAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal profitLossAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal profitLossRate;

    @Column(nullable = false)
    private Instant capturedAt;
}
