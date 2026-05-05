package com.upmudoum.trade.domain.master.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeMasterSourceDefinition {

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
