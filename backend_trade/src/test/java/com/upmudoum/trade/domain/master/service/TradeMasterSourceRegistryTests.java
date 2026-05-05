package com.upmudoum.trade.domain.master.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.master.vo.TradeMasterParserStrategy;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import org.junit.jupiter.api.Test;

class TradeMasterSourceRegistryTests {

    private final TradeMasterSourceRegistry registry = new TradeMasterSourceRegistry();

    @Test
    void registersEveryMasterType() {
        assertThat(registry.allTypesRegistered()).isTrue();
        assertThat(registry.typeDtos()).hasSize(TradeMasterType.values().length);
    }

    @Test
    void defaultImportTargetsComeFromRegistry() {
        assertThat(registry.defaultImportTargets())
                .containsExactly(TradeMasterType.KOSPI, TradeMasterType.KOSDAQ);
    }

    @Test
    void exposesSupportedCandidateMetadata() {
        assertThat(registry.get(TradeMasterType.DOMESTIC_BOND).getSourceUrl())
                .endsWith("/bond_code.mst.zip");
        assertThat(registry.get(TradeMasterType.OVERSEAS_FUTURE).getSourceUrl())
                .endsWith("/ffcode.mst.zip");
        assertThat(registry.get(TradeMasterType.OVERSEAS_STOCK).isEnabled()).isTrue();
        assertThat(registry.sourceUrls(TradeMasterType.OVERSEAS_STOCK))
                .hasSize(11)
                .contains(
                        "https://new.real.download.dws.co.kr/common/master/nasmst.cod.zip",
                        "https://new.real.download.dws.co.kr/common/master/hsxmst.cod.zip"
                );
        assertThat(registry.get(TradeMasterType.ETF_ETN).getParserStrategy())
                .isEqualTo(TradeMasterParserStrategy.NOT_IMPLEMENTED);
    }
}
