package com.upmudoum.trade.domain.analysis.entity;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "market_ranking_snapshot")
public class MarketRankingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate baseDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "varchar(30)")
    private TradeMasterType masterType;

    @Column(nullable = false, length = 30)
    private String itemCode;

    @Column(nullable = false, length = 200)
    private String itemName;

    @Column(nullable = false, length = 50)
    private String marketCode;

    @Column(length = 30)
    private String countryCode;

    @Column(length = 100)
    private String sectorName;

    @Column(precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal changeAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal changeRate;

    @Column(precision = 19, scale = 4)
    private BigDecimal volume;

    @Column(precision = 19, scale = 4)
    private BigDecimal turnover;

    @Column(precision = 19, scale = 4)
    private BigDecimal marketCap;

    @Column(precision = 19, scale = 4)
    private BigDecimal high52WeekPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal low52WeekPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal volatility;

    @Column(nullable = false)
    private Instant capturedAt;
}
