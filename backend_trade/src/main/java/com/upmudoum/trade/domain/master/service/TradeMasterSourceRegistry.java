package com.upmudoum.trade.domain.master.service;

import com.upmudoum.trade.domain.master.dto.TradeMasterTypeDto;
import com.upmudoum.trade.domain.master.vo.TradeMasterFileFormat;
import com.upmudoum.trade.domain.master.vo.TradeMasterParserStrategy;
import com.upmudoum.trade.domain.master.vo.TradeMasterSourceDefinition;
import com.upmudoum.trade.domain.master.vo.TradeMasterSourceType;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TradeMasterSourceRegistry {

    private static final String MASTER_BASE_URL = "https://new.real.download.dws.co.kr/common/master/";
    private static final String CP949 = "CP949";
    private static final List<String> OVERSEAS_STOCK_MARKET_CODES = List.of(
            "nas", "nys", "ams", "shs", "shi", "szs", "szi", "tse", "hks", "hnx", "hsx"
    );

    private final Map<TradeMasterType, TradeMasterSourceDefinition> definitions = new EnumMap<>(TradeMasterType.class);

    public TradeMasterSourceRegistry() {
        register(domesticStock(TradeMasterType.KOSPI, "KOSPI Domestic Stock", "kospi_code.mst.zip", true, true));
        register(domesticStock(TradeMasterType.KOSDAQ, "KOSDAQ Domestic Stock", "kosdaq_code.mst.zip", true, true));
        register(domesticStock(TradeMasterType.KONEX, "KONEX Domestic Stock", "konex_code.mst.zip", false, true));
        register(definition(TradeMasterType.ELW, "Domestic ELW", "DOMESTIC_DERIVATIVE",
                "elw_code.mst.zip", TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.DOMESTIC_ELW_FIXED_WIDTH, true, false, true,
                "Domestic ELW master from stocks_info/domestic_elw_code.py."));
        register(definition(TradeMasterType.ETF_ETN, "Domestic ETF/ETN", "DOMESTIC_ETP",
                null, TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.NOT_IMPLEMENTED, false, false, false,
                "Candidate type. No confirmed standalone ETF/ETN master URL in the surveyed reference."));
        register(definition(TradeMasterType.DOMESTIC_INDEX, "Domestic Sector Index", "DOMESTIC_INDEX",
                "idxcode.mst.zip", TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.SECTOR_FIXED_WIDTH, true, false, true,
                "Domestic sector/index code master from stocks_info/sector_code.py."));
        register(definition(TradeMasterType.DOMESTIC_BOND, "Domestic Bond", "DOMESTIC_BOND",
                "bond_code.mst.zip", TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.DOMESTIC_BOND_FIXED_WIDTH, true, false, true,
                "Domestic bond master from stocks_info/domestic_bond_code.py."));
        register(definition(TradeMasterType.DOMESTIC_INDEX_FUTURE_OPTION, "Domestic Index Future/Option", "DOMESTIC_DERIVATIVE",
                "fo_idx_code_mts.mst.zip", TradeMasterFileFormat.PIPE_MST_ZIP,
                TradeMasterParserStrategy.PIPE_DELIMITED_MASTER, true, false, true,
                "Index futures/options master from stocks_info/domestic_index_future_code.py."));
        register(definition(TradeMasterType.DOMESTIC_STOCK_FUTURE_OPTION, "Domestic Stock Future/Option", "DOMESTIC_DERIVATIVE",
                "fo_stk_code_mts.mst.zip", TradeMasterFileFormat.PIPE_MST_ZIP,
                TradeMasterParserStrategy.PIPE_DELIMITED_MASTER, true, false, true,
                "Stock futures/options master from stocks_info/domestic_stock_future_code.py."));
        register(definition(TradeMasterType.DOMESTIC_COMMODITY_FUTURE_OPTION, "Domestic Commodity Future/Option", "DOMESTIC_DERIVATIVE",
                "fo_com_code.mst.zip", TradeMasterFileFormat.PIPE_MST_ZIP,
                TradeMasterParserStrategy.PIPE_DELIMITED_MASTER, true, false, true,
                "Commodity futures/options master from stocks_info/domestic_commodity_future_code.py."));
        register(definition(TradeMasterType.DOMESTIC_CME_FUTURE, "Domestic CME Future", "DOMESTIC_DERIVATIVE",
                "fo_cme_code.mst.zip", TradeMasterFileFormat.PIPE_MST_ZIP,
                TradeMasterParserStrategy.PIPE_DELIMITED_MASTER, true, false, true,
                "CME-linked domestic future master from stocks_info/domestic_cme_future_code.py."));
        register(definition(TradeMasterType.DOMESTIC_EUREX_OPTION, "Domestic Eurex Option", "DOMESTIC_DERIVATIVE",
                "fo_eurex_code.mst.zip", TradeMasterFileFormat.PIPE_MST_ZIP,
                TradeMasterParserStrategy.PIPE_DELIMITED_MASTER, true, false, true,
                "Eurex option master from stocks_info/domestic_eurex_option_code.py."));
        register(definition(TradeMasterType.OVERSEAS_STOCK, "Overseas Stock", "OVERSEAS_STOCK",
                "{market}mst.cod.zip", TradeMasterFileFormat.COD_ZIP,
                TradeMasterParserStrategy.OVERSEAS_STOCK_TAB, true, false, true,
                "Sequentially downloads every overseas stock market code: nas/nys/ams/shs/shi/szs/szi/tse/hks/hnx/hsx."));
        register(definition(TradeMasterType.OVERSEAS_FUTURE, "Overseas Future", "OVERSEAS_DERIVATIVE",
                "ffcode.mst.zip", TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.OVERSEAS_STOCK_TAB, true, false, true,
                "Overseas future master from stocks_info/overseas_future_code.py."));
        register(definition(TradeMasterType.OVERSEAS_OPTION, "Overseas Option", "OVERSEAS_DERIVATIVE",
                null, TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.NOT_IMPLEMENTED, false, false, false,
                "Candidate from stocks_info/overseas option header. Download URL/parser not confirmed."));
        register(definition(TradeMasterType.OVERSEAS_INDEX, "Overseas Index", "OVERSEAS_INDEX",
                "frgn_code.mst.zip", TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.TAB_DELIMITED_MASTER, true, false, false,
                "Overseas index master from stocks_info/overseas_index_code.py. Parser needs sample validation."));
        register(definition(TradeMasterType.OVERSEAS_STOCK_OPTION, "Overseas Stock Option", "OVERSEAS_DERIVATIVE",
                null, TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.NOT_IMPLEMENTED, false, false, false,
                "Candidate from stocks_info/overseas stock option header. Download URL/parser not confirmed."));
        register(definition(TradeMasterType.SECTOR_INDEX, "Sector Code", "DOMESTIC_INDEX",
                "idxcode.mst.zip", TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.SECTOR_FIXED_WIDTH, true, false, true,
                "Sector code master from stocks_info/sector_code.py."));
        register(definition(TradeMasterType.THEME_INDEX, "Theme Code", "DOMESTIC_THEME",
                "theme_code.mst.zip", TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.THEME_FIXED_WIDTH, true, false, true,
                "Theme code master from stocks_info/theme_code.py."));
        register(definition(TradeMasterType.MEMBER, "Member Code", "DOMESTIC_MEMBER",
                "memcode.mst", TradeMasterFileFormat.MST,
                TradeMasterParserStrategy.NOT_IMPLEMENTED, false, false, false,
                "Member code master exists in stocks_info/member_code.py but is not an item master import target."));
    }

    public List<TradeMasterSourceDefinition> definitions() {
        return definitions.values().stream()
                .sorted(Comparator.comparing(definition -> definition.getMasterType().ordinal()))
                .toList();
    }

    public List<TradeMasterTypeDto> typeDtos() {
        return definitions().stream()
                .map(this::toDto)
                .toList();
    }

    public TradeMasterSourceDefinition get(TradeMasterType masterType) {
        TradeMasterSourceDefinition definition = definitions.get(masterType);
        if (definition == null) {
            throw new IllegalArgumentException("master source definition is not registered for " + masterType);
        }
        return definition;
    }

    public TradeMasterTypeDto getDto(TradeMasterType masterType) {
        return toDto(get(masterType));
    }

    public List<TradeMasterType> defaultImportTargets() {
        return definitions().stream()
                .filter(TradeMasterSourceDefinition::isEnabled)
                .filter(TradeMasterSourceDefinition::isDefaultImportTargetYn)
                .map(TradeMasterSourceDefinition::getMasterType)
                .toList();
    }

    public List<String> templateMarketCodes(TradeMasterType masterType) {
        if (masterType == TradeMasterType.OVERSEAS_STOCK) {
            return OVERSEAS_STOCK_MARKET_CODES;
        }
        return List.of();
    }

    public List<String> sourceUrls(TradeMasterType masterType) {
        TradeMasterSourceDefinition definition = get(masterType);
        if (definition.getSourceUrl() == null || definition.getSourceUrl().isBlank()) {
            return List.of();
        }
        if (!definition.getSourceUrl().contains("{market}")) {
            return List.of(definition.getSourceUrl());
        }
        return templateMarketCodes(masterType).stream()
                .map(marketCode -> definition.getSourceUrl().replace("{market}", marketCode))
                .toList();
    }

    public boolean allTypesRegistered() {
        return Arrays.stream(TradeMasterType.values()).allMatch(definitions::containsKey);
    }

    private TradeMasterSourceDefinition domesticStock(
            TradeMasterType masterType,
            String displayName,
            String sourceFileName,
            boolean defaultImportTargetYn,
            boolean parserImplementedYn
    ) {
        return definition(masterType, displayName, "DOMESTIC_STOCK",
                sourceFileName, TradeMasterFileFormat.MST_ZIP,
                TradeMasterParserStrategy.DOMESTIC_STOCK_FIXED_WIDTH, true, defaultImportTargetYn, parserImplementedYn,
                "Domestic stock master from open-trading-api-main stocks_info.");
    }

    private TradeMasterSourceDefinition definition(
            TradeMasterType masterType,
            String displayName,
            String marketCategory,
            String sourceFileName,
            TradeMasterFileFormat fileFormat,
            TradeMasterParserStrategy parserStrategy,
            boolean enabled,
            boolean defaultImportTargetYn,
            boolean parserImplementedYn,
            String description
    ) {
        TradeMasterSourceDefinition definition = new TradeMasterSourceDefinition();
        definition.setMasterType(masterType);
        definition.setDisplayName(displayName);
        definition.setMarketCategory(marketCategory);
        definition.setSourceType(sourceFileName != null && sourceFileName.contains("{market}")
                ? TradeMasterSourceType.DOWNLOAD_URL_TEMPLATE
                : TradeMasterSourceType.DOWNLOAD_URL);
        definition.setSourceUrl(sourceFileName == null ? null : MASTER_BASE_URL + sourceFileName);
        definition.setFileFormat(fileFormat);
        definition.setCharset(CP949);
        definition.setParserStrategy(parserStrategy);
        definition.setEnabled(enabled);
        definition.setDefaultImportTargetYn(defaultImportTargetYn);
        definition.setParserImplementedYn(parserImplementedYn);
        definition.setDescription(description);
        return definition;
    }

    private void register(TradeMasterSourceDefinition definition) {
        definitions.put(definition.getMasterType(), definition);
    }

    private TradeMasterTypeDto toDto(TradeMasterSourceDefinition definition) {
        TradeMasterTypeDto dto = new TradeMasterTypeDto();
        dto.setMasterType(definition.getMasterType());
        dto.setDisplayName(definition.getDisplayName());
        dto.setMarketCategory(definition.getMarketCategory());
        dto.setSourceType(definition.getSourceType());
        dto.setSourceUrl(definition.getSourceUrl());
        dto.setFileFormat(definition.getFileFormat());
        dto.setCharset(definition.getCharset());
        dto.setParserStrategy(definition.getParserStrategy());
        dto.setEnabled(definition.isEnabled());
        dto.setDefaultImportTargetYn(definition.isDefaultImportTargetYn());
        dto.setParserImplementedYn(definition.isParserImplementedYn());
        dto.setDescription(definition.getDescription());
        return dto;
    }
}
