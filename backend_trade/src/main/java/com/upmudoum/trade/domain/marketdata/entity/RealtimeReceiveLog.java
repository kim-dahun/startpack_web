package com.upmudoum.trade.domain.marketdata.entity;

import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "realtime_receive_log")
public class RealtimeReceiveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TradeRealtimeEventType type;

    @Column(nullable = false, length = 30)
    private String itemCode;

    @Column(nullable = false)
    private Instant occurredAt;

    @Lob
    @Column(nullable = false)
    private String payloadJson;

    @Column(nullable = false)
    private Instant receivedAt;
}
