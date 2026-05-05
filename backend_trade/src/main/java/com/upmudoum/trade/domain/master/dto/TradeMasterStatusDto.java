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
public class TradeMasterStatusDto {

    private TradeMasterType masterType;
    private long itemCount;
    private Instant lastImportedAt;
    private String lastSourceFileName;
    private String lastSourceVersion;
    private TradeMasterImportStatus lastImportStatus;
    private boolean lastImportSuccess;
}
