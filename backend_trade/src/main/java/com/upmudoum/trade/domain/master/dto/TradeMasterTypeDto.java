package com.upmudoum.trade.domain.master.dto;

import com.upmudoum.trade.domain.master.vo.TradeMasterFileFormat;
import com.upmudoum.trade.domain.master.vo.TradeMasterParserStrategy;
import com.upmudoum.trade.domain.master.vo.TradeMasterSourceType;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeMasterTypeDto {

    private TradeMasterType masterType;
    private String displayName;
    private String marketCategory;
    private TradeMasterSourceType sourceType;
    private String sourceUrl;
    private TradeMasterFileFormat fileFormat;
    private String charset;
    private TradeMasterParserStrategy parserStrategy;
    private boolean enabled;
    private boolean defaultImportTargetYn;
    private boolean parserImplementedYn;
    private String description;
}
