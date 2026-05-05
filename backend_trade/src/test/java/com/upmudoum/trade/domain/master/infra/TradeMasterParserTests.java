package com.upmudoum.trade.domain.master.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.upmudoum.trade.domain.master.dto.TradeMasterImportRowDto;
import com.upmudoum.trade.domain.master.service.TradeMasterSourceRegistry;
import com.upmudoum.trade.domain.master.vo.TradeMasterType;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.jupiter.api.Test;

class TradeMasterParserTests {

    private final TradeMasterSourceRegistry sourceRegistry = new TradeMasterSourceRegistry();
    private final TradeMasterParser parser = new TradeMasterParser(sourceRegistry);

    @Test
    void parsesKospiFixedWidthMaster() {
        byte[] line = new byte[300];
        put(line, 0, 9, "A005930");
        put(line, 9, 21, "KR7005930003");
        put(line, 21, 61, "삼성전자");

        List<TradeMasterImportRowDto> rows = parser.parse(TradeMasterType.KOSPI, line);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getItemCode()).isEqualTo("005930");
        assertThat(rows.get(0).getItemName()).isEqualTo("삼성전자");
        assertThat(rows.get(0).getMarketCode()).isEqualTo("KOSPI");
        assertThat(rows.get(0).getCountryCode()).isEqualTo("KR");
    }

    @Test
    void parsesKonexFromRegistryStrategy() {
        byte[] line = new byte[260];
        put(line, 0, 9, "A123456");
        put(line, 9, 21, "KR7123456000");
        put(line, 21, 61, "Konex Sample");

        List<TradeMasterImportRowDto> rows = parser.parse(TradeMasterType.KONEX, line);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getItemCode()).isEqualTo("123456");
        assertThat(rows.get(0).getMarketCode()).isEqualTo("KONEX");
    }

    @Test
    void rejectsParserNotImplementedType() {
        assertThatThrownBy(() -> parser.parse(TradeMasterType.ETF_ETN, new byte[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("parser is not implemented");
    }

    @Test
    void parsesOverseasStockTabMaster() {
        String line = "US\tNAS\tNASD\tNASDAQ\tAAPL\tAAPL\tApple\tApple Inc\t2\tUSD";

        List<TradeMasterImportRowDto> rows = parser.parse(TradeMasterType.OVERSEAS_STOCK, line.getBytes(Charset.forName("CP949")));

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getItemCode()).isEqualTo("AAPL");
        assertThat(rows.get(0).getItemName()).isEqualTo("Apple");
        assertThat(rows.get(0).getMarketCode()).isEqualTo("OVERSEAS_STOCK");
        assertThat(rows.get(0).getCountryCode()).isEqualTo("US");
    }

    private void put(byte[] target, int start, int end, String value) {
        byte[] encoded = value.getBytes(Charset.forName("EUC-KR"));
        System.arraycopy(encoded, 0, target, start, Math.min(encoded.length, end - start));
        for (int i = start + encoded.length; i < end; i++) {
            target[i] = ' ';
        }
    }
}
