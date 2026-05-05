package com.upmudoum.trade.domain.item.entity;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "item_master")
public class ItemMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "varchar(30)")
    private TradeMasterType masterType;

    @Column(nullable = false, unique = true, length = 30)
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
    private java.math.BigDecimal per;

    @Column(precision = 19, scale = 4)
    private java.math.BigDecimal pbr;

    @Column(precision = 19, scale = 4)
    private java.math.BigDecimal eps;

    @Column(precision = 19, scale = 4)
    private java.math.BigDecimal bps;

    @Column(precision = 19, scale = 4)
    private java.math.BigDecimal salesAmount;

    @Column(precision = 19, scale = 4)
    private java.math.BigDecimal operatingProfit;

    @Column(precision = 19, scale = 4)
    private java.math.BigDecimal marketCap;

    @Column(name = "high52_week_price", precision = 19, scale = 4)
    private java.math.BigDecimal high52WeekPrice;

    @Column(name = "low52_week_price", precision = 19, scale = 4)
    private java.math.BigDecimal low52WeekPrice;

    @Column(length = 200)
    private String sourceFileName;

    private Instant sourceDownloadedAt;

    @Column(length = 100)
    private String sourceVersion;

    @Column(columnDefinition = "text")
    private String rawJson;

    @Column(nullable = false)
    private Instant syncedAt;
}
