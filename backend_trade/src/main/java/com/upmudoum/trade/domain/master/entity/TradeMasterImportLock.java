package com.upmudoum.trade.domain.master.entity;

import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "trade_master_import_lock")
public class TradeMasterImportLock {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "varchar(30)")
    private TradeMasterType masterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradeMasterImportStatus importStatus;

    private Long historyId;

    @Column(nullable = false)
    private Instant lastRequestedAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
