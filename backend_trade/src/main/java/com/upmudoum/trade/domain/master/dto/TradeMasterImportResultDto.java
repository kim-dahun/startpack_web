package com.upmudoum.trade.domain.master.dto;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeMasterImportResultDto {

    private Long historyId;
    private TradeMasterType masterType;
    private String sourceFileName;
    private String sourceVersion;
    private TradeMasterImportStatus importStatus;
    private int importedCount;
    private Instant startedAt;
    private Instant finishedAt;
    private boolean success;
    private String failureReason;
}
