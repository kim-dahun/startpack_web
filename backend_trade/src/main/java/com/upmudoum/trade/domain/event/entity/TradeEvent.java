package com.upmudoum.trade.domain.event.entity;

import com.upmudoum.trade.domain.event.vo.TradeEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "trade_event")
public class TradeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TradeEventType eventType;

    @Column(length = 30)
    private String itemCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String rawJson;

    @Column(nullable = false)
    private Instant createdAt;
}
