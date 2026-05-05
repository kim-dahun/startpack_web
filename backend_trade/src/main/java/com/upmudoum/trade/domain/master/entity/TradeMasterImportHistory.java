package com.upmudoum.trade.domain.master.entity;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
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
@Table(name = "trade_master_import_history")
public class TradeMasterImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "varchar(30)")
    private TradeMasterType masterType;

    @Column(nullable = false, length = 200)
    private String sourceFileName;

    @Column(length = 100)
    private String sourceVersion;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TradeMasterImportStatus importStatus;

    @Column(nullable = false)
    private int importedCount;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false)
    private Instant finishedAt;

    @Column(nullable = false)
    private boolean success;

    @Column(columnDefinition = "text")
    private String failureReason;
}
