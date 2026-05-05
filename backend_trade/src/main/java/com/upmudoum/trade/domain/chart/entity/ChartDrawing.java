package com.upmudoum.trade.domain.chart.entity;

import com.upmudoum.trade.domain.chart.vo.ChartDrawingType;
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
@Table(name = "chart_drawing")
public class ChartDrawing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, length = 30)
    private String itemCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChartDrawingType drawingType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal startPrice;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal endPrice;

    @Column(length = 200)
    private String memo;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
