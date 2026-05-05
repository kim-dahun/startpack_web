package com.upmudoum.trade.domain.master.dto;

import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeMasterDownloadImportRequest {

    @NotNull
    private TradeMasterType masterType;

    private String sourceUrl;

    private String sourceVersion;
}
