package com.upmudoum.trade.domain.kis.entity;

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
@Table(name = "kis_api_call_log")
public class KisApiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Column(nullable = false, length = 10)
    private String method;

    private int statusCode;
    private long elapsedMillis;

    @Column(length = 100)
    private String errorCode;

    @Column(length = 4000)
    private String errorMessage;

    @Column(nullable = false)
    private Instant calledAt;
}
