package com.upmudoum.trade.domain.item.infra;

import com.upmudoum.trade.domain.item.dto.ItemChartCandleDto;
import com.upmudoum.trade.domain.item.dto.ItemDto;
import com.upmudoum.trade.domain.item.dto.ItemPriceDto;
import com.upmudoum.trade.domain.item.dto.OrderbookDto;
import com.upmudoum.trade.domain.item.dto.OrderbookLevelDto;
import com.upmudoum.trade.domain.kis.infra.KisResponseExtractor;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KisItemMapper {

    public List<ItemDto> toItems(Map<String, Object> response) {
        List<Map<String, Object>> output = KisResponseExtractor.list(response, "output");
        if (output.isEmpty()) {
            return List.of();
        }
        return output.stream()
                .map(this::toItem)
                .toList();
    }

    public ItemDto toItem(Map<String, Object> response, String fallbackItemCode) {
        Map<String, Object> output = KisResponseExtractor.object(response, "output");
        if (output.isEmpty()) {
            return null;
        }
        ItemDto item = toItem(output);
        if (item.getItemCode().isBlank()) {
            item.setItemCode(fallbackItemCode);
        }
        return item;
    }

    private ItemDto toItem(Map<String, Object> row) {
        return new ItemDto(
                KisResponseExtractor.text(row, "stck_shrn_iscd", "pdno", "itemCode"),
                KisResponseExtractor.text(row, "hts_kor_isnm", "prdt_name", "itemName"),
                KisResponseExtractor.text(row, "rprs_mrkt_kor_name", "marketCode")
        );
    }

    public ItemPriceDto toPrice(String itemCode, Map<String, Object> response) {
        Map<String, Object> output = KisResponseExtractor.object(response, "output");
        ItemPriceDto dto = new ItemPriceDto();
        dto.setItemCode(itemCode);
        dto.setItemName(KisResponseExtractor.text(output, "hts_kor_isnm", "itemName"));
        dto.setCurrentPrice(KisResponseExtractor.decimal(output, "stck_prpr", "currentPrice"));
        dto.setChangeAmount(KisResponseExtractor.decimal(output, "prdy_vrss", "changeAmount"));
        dto.setChangeRate(KisResponseExtractor.decimal(output, "prdy_ctrt", "changeRate"));
        dto.setOpenPrice(KisResponseExtractor.decimal(output, "stck_oprc", "openPrice"));
        dto.setHighPrice(KisResponseExtractor.decimal(output, "stck_hgpr", "highPrice"));
        dto.setLowPrice(KisResponseExtractor.decimal(output, "stck_lwpr", "lowPrice"));
        dto.setAccumulatedVolume(KisResponseExtractor.decimal(output, "acml_vol", "accumulatedVolume").longValue());
        dto.setRaw(response);
        return dto;
    }

    public OrderbookDto toOrderbook(String itemCode, Map<String, Object> response) {
        Map<String, Object> output = KisResponseExtractor.object(response, "output1");
        OrderbookDto dto = new OrderbookDto();
        dto.setItemCode(itemCode);
        dto.setReceivedAt(Instant.now());
        dto.setLevels(java.util.stream.IntStream.rangeClosed(1, 10)
                .mapToObj(level -> toOrderbookLevel(output, level))
                .toList());
        dto.setRaw(response);
        return dto;
    }

    public List<ItemChartCandleDto> toChart(String itemCode, String periodType, Map<String, Object> response) {
        return KisResponseExtractor.list(response, "output2").stream()
                .map(row -> toChartCandle(itemCode, periodType, row))
                .toList();
    }

    private OrderbookLevelDto toOrderbookLevel(Map<String, Object> output, int level) {
        OrderbookLevelDto dto = new OrderbookLevelDto();
        dto.setLevel(level);
        dto.setAskPrice(KisResponseExtractor.decimal(output, "askp" + level));
        dto.setAskQuantity(KisResponseExtractor.decimal(output, "askp_rsqn" + level).longValue());
        dto.setBidPrice(KisResponseExtractor.decimal(output, "bidp" + level));
        dto.setBidQuantity(KisResponseExtractor.decimal(output, "bidp_rsqn" + level).longValue());
        return dto;
    }

    private ItemChartCandleDto toChartCandle(String itemCode, String periodType, Map<String, Object> row) {
        ItemChartCandleDto dto = new ItemChartCandleDto();
        dto.setItemCode(itemCode);
        dto.setPeriodType(periodType);
        dto.setBaseDate(LocalDate.parse(KisResponseExtractor.text(row, "stck_bsop_date", "baseDate"), java.time.format.DateTimeFormatter.BASIC_ISO_DATE));
        dto.setOpenPrice(KisResponseExtractor.decimal(row, "stck_oprc", "openPrice"));
        dto.setHighPrice(KisResponseExtractor.decimal(row, "stck_hgpr", "highPrice"));
        dto.setLowPrice(KisResponseExtractor.decimal(row, "stck_lwpr", "lowPrice"));
        dto.setClosePrice(KisResponseExtractor.decimal(row, "stck_clpr", "closePrice"));
        dto.setVolume(KisResponseExtractor.decimal(row, "acml_vol", "volume").longValue());
        return dto;
    }
}
