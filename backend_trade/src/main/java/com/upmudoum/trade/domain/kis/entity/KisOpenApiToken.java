package com.upmudoum.trade.domain.kis.entity;

import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "kis_open_api_token",
        uniqueConstraints = @UniqueConstraint(name = "uk_kis_open_api_token_trade_mode", columnNames = "trade_mode")
)
public class KisOpenApiToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_mode", nullable = false, length = 20)
    private KisTradeMode tradeMode;

    @Column(nullable = false, length = 4000)
    private String accessToken;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
