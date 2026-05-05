package com.upmudoum.trade.domain.master.dto;

import com.upmudoum.trade.domain.master.vo.TradeMasterImportStatus;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeMasterImportJobDto {

    private Long historyId;
    private TradeMasterType masterType;
    private TradeMasterImportStatus importStatus;
    private Instant submittedAt;
    private String message;
}
