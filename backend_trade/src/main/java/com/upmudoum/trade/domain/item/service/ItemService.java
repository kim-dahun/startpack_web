package com.upmudoum.trade.domain.item.service;

import com.upmudoum.trade.domain.item.dto.ItemChartCandleDto;
import com.upmudoum.trade.domain.item.dto.ItemDto;
import com.upmudoum.trade.domain.item.dto.ItemIndicatorDto;
import com.upmudoum.trade.domain.item.dto.ItemMetricsDto;
import com.upmudoum.trade.domain.item.dto.ItemPriceDto;
import com.upmudoum.trade.domain.item.dto.ItemSearchResultDto;
import com.upmudoum.trade.domain.item.dto.ItemSummaryDto;
import com.upmudoum.trade.domain.item.dto.MultiQuoteRequest;
import com.upmudoum.trade.domain.item.dto.MultiOrderbookRequest;
import com.upmudoum.trade.domain.item.dto.OrderbookDto;
import com.upmudoum.trade.domain.item.infra.KisItemMapper;
import com.upmudoum.trade.domain.item.querydsl.ItemMasterQueryRepository;
import com.upmudoum.trade.domain.item.repository.ItemMasterRepository;
import com.upmudoum.trade.domain.kis.infra.KisQueryFactory;
import com.upmudoum.trade.domain.kis.infra.KisRestClient;
import com.upmudoum.trade.domain.kis.vo.KisEndpoint;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final KisRestClient kisRestClient;
    private final KisItemMapper kisItemMapper;
    private final KisQueryFactory kisQueryFactory;
    private final ItemMasterService itemMasterService;
    private final ItemMasterRepository itemMasterRepository;
    private final ItemMasterQueryRepository itemMasterQueryRepository;

    public ItemService(
            KisRestClient kisRestClient,
            KisItemMapper kisItemMapper,
            KisQueryFactory kisQueryFactory,
            ItemMasterService itemMasterService,
            ItemMasterRepository itemMasterRepository,
            ItemMasterQueryRepository itemMasterQueryRepository
    ) {
        this.kisRestClient = kisRestClient;
        this.kisItemMapper = kisItemMapper;
        this.kisQueryFactory = kisQueryFactory;
        this.itemMasterService = itemMasterService;
        this.itemMasterRepository = itemMasterRepository;
        this.itemMasterQueryRepository = itemMasterQueryRepository;
    }

    public List<ItemDto> findItems(String keyword, KisTradeMode tradeMode) {
        List<ItemDto> kisItems = kisItemMapper.toItems(kisRestClient.get(KisEndpoint.SEARCH_INFO, kisQueryFactory.itemSearch(keyword), tradeMode));
        if (!kisItems.isEmpty()) {
            itemMasterService.saveAll(kisItems);
            return kisItems;
        }
        List<ItemDto> fallback = List.of(
                new ItemDto("005930", "삼성전자", "KOSPI"),
                new ItemDto("000660", "SK하이닉스", "KOSPI")
        ).stream()
                .filter(item -> keyword == null || keyword.isBlank()
                        || item.getItemCode().contains(keyword)
                        || item.getItemName().contains(keyword))
                .toList();
        itemMasterService.saveAll(fallback);
        return fallback;
    }

    public ItemDto findItem(String itemCode, KisTradeMode tradeMode) {
        ItemDto kisItem = kisItemMapper.toItem(kisRestClient.get(KisEndpoint.INQUIRE_PRICE, kisQueryFactory.itemPrice(itemCode), tradeMode), itemCode);
        if (kisItem != null) {
            itemMasterService.save(kisItem);
            return kisItem;
        }
        ItemDto fallback = findItems(itemCode, tradeMode).stream()
                .findFirst()
                .orElse(new ItemDto(itemCode, "미등록 종목", "UNKNOWN"));
        itemMasterService.save(fallback);
        return fallback;
    }

    public ItemPriceDto findPrice(String itemCode, KisTradeMode tradeMode) {
        return kisItemMapper.toPrice(
                itemCode,
                kisRestClient.get(KisEndpoint.INQUIRE_PRICE, kisQueryFactory.itemPrice(itemCode), tradeMode)
        );
    }

    public Map<String, ItemSearchResultDto> searchItems(String keyword, KisTradeMode tradeMode) {
        List<ItemSearchResultDto> candidates = itemMasterQueryRepository.searchByCodeOrName(keyword, 5);
        Map<String, ItemSearchResultDto> result = new LinkedHashMap<>();
        for (ItemSearchResultDto candidate : candidates) {
            ItemPriceDto quote = findPrice(candidate.getItemCode(), tradeMode);
            enrichSearchResult(candidate, quote);
            result.put(candidate.getItemCode(), candidate);
        }
        return result;
    }

    public Map<String, ItemPriceDto> findQuotes(MultiQuoteRequest request) {
        Map<String, ItemPriceDto> result = new LinkedHashMap<>();
        if (request.getItemCodes() == null) {
            return result;
        }
        for (String itemCode : request.getItemCodes()) {
            result.put(itemCode, findPrice(itemCode, request.getTradeMode()));
        }
        return result;
    }

    public OrderbookDto findOrderbook(String itemCode, KisTradeMode tradeMode) {
        return kisItemMapper.toOrderbook(
                itemCode,
                kisRestClient.get(KisEndpoint.INQUIRE_ORDERBOOK, kisQueryFactory.orderbook(itemCode), tradeMode)
        );
    }

    public List<OrderbookDto> findOrderbooks(MultiOrderbookRequest request) {
        if (request.getItemCodes() == null || request.getItemCodes().isEmpty()) {
            return List.of();
        }
        return request.getItemCodes().stream()
                .map(itemCode -> findOrderbook(itemCode, request.getTradeMode()))
                .toList();
    }

    public List<ItemChartCandleDto> findChart(String itemCode, String periodType, LocalDate from, LocalDate to, KisTradeMode tradeMode) {
        return kisItemMapper.toChart(
                itemCode,
                periodType,
                kisRestClient.get(KisEndpoint.INQUIRE_DAILY_ITEM_CHART_PRICE, kisQueryFactory.itemChart(itemCode, periodType, from, to), tradeMode)
        );
    }

    public ItemSummaryDto summary(String itemCode, KisTradeMode tradeMode) {
        ItemSummaryDto dto = new ItemSummaryDto();
        dto.setQuote(findPrice(itemCode, tradeMode));
        dto.setMetrics(metrics(itemCode));
        return dto;
    }

    public ItemMetricsDto metrics(String itemCode) {
        return itemMasterRepository.findByItemCode(itemCode)
                .map(this::toMetrics)
                .orElseGet(() -> {
                    ItemMetricsDto dto = new ItemMetricsDto();
                    dto.setItemCode(itemCode);
                    return dto;
                });
    }

    public ItemIndicatorDto indicators(String itemCode, String periodType, LocalDate from, LocalDate to, KisTradeMode tradeMode) {
        List<ItemChartCandleDto> candles = findChart(itemCode, periodType, from, to, tradeMode);
        ItemIndicatorDto dto = new ItemIndicatorDto();
        dto.setItemCode(itemCode);
        dto.setPeriodType(periodType);
        dto.setMovingAverages(Map.of(
                "ma5", movingAverage(candles, 5),
                "ma20", movingAverage(candles, 20),
                "ma60", movingAverage(candles, 60),
                "ma120", movingAverage(candles, 120)
        ));
        dto.setRsi(rsi(candles, 14));
        dto.setMacd(macd(candles, 12, 26));
        dto.setMacdSignal(BigDecimal.ZERO);
        dto.setMacdHistogram(dto.getMacd().subtract(dto.getMacdSignal()));
        BigDecimal ma20 = movingAverage(candles, 20);
        BigDecimal deviation = standardDeviation(candles, 20, ma20);
        dto.setBollingerMiddle(ma20);
        dto.setBollingerUpper(ma20.add(deviation.multiply(BigDecimal.valueOf(2))));
        dto.setBollingerLower(ma20.subtract(deviation.multiply(BigDecimal.valueOf(2))));
        dto.setAtr(atr(candles, 14));
        dto.setStochasticK(stochasticK(candles, 14));
        dto.setStochasticD(dto.getStochasticK());
        dto.setObv(obv(candles));
        dto.setMfi(BigDecimal.ZERO);
        return dto;
    }

    private void enrichSearchResult(ItemSearchResultDto dto, ItemPriceDto quote) {
        dto.setCurrentPrice(quote.getCurrentPrice());
        dto.setChangeAmount(quote.getChangeAmount());
        dto.setChangeRate(quote.getChangeRate());
        dto.setAccumulatedVolume(quote.getAccumulatedVolume());
    }

    private ItemMetricsDto toMetrics(com.upmudoum.trade.domain.item.entity.ItemMaster item) {
        ItemMetricsDto dto = new ItemMetricsDto();
        dto.setItemCode(item.getItemCode());
        dto.setItemName(item.getItemName());
        dto.setMarketCode(item.getMarketCode());
        dto.setSectorName(item.getSectorName());
        dto.setPer(item.getPer());
        dto.setPbr(item.getPbr());
        dto.setEps(item.getEps());
        dto.setBps(item.getBps());
        dto.setSalesAmount(item.getSalesAmount());
        dto.setOperatingProfit(item.getOperatingProfit());
        dto.setMarketCap(item.getMarketCap());
        dto.setHigh52WeekPrice(item.getHigh52WeekPrice());
        dto.setLow52WeekPrice(item.getLow52WeekPrice());
        return dto;
    }

    private BigDecimal movingAverage(List<ItemChartCandleDto> candles, int count) {
        List<ItemChartCandleDto> window = tail(candles, count);
        if (window.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = window.stream().map(ItemChartCandleDto::getClosePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(window.size()), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal rsi(List<ItemChartCandleDto> candles, int count) {
        List<ItemChartCandleDto> window = tail(candles, count + 1);
        if (window.size() < 2) {
            return BigDecimal.ZERO;
        }
        BigDecimal gain = BigDecimal.ZERO;
        BigDecimal loss = BigDecimal.ZERO;
        for (int i = 1; i < window.size(); i++) {
            BigDecimal diff = window.get(i).getClosePrice().subtract(window.get(i - 1).getClosePrice());
            if (diff.signum() >= 0) {
                gain = gain.add(diff);
            } else {
                loss = loss.add(diff.abs());
            }
        }
        if (loss.signum() == 0) {
            return BigDecimal.valueOf(100);
        }
        BigDecimal rs = gain.divide(loss, 4, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 4, RoundingMode.HALF_UP));
    }

    private BigDecimal macd(List<ItemChartCandleDto> candles, int shortCount, int longCount) {
        return movingAverage(candles, shortCount).subtract(movingAverage(candles, longCount));
    }

    private BigDecimal standardDeviation(List<ItemChartCandleDto> candles, int count, BigDecimal average) {
        List<ItemChartCandleDto> window = tail(candles, count);
        if (window.isEmpty()) {
            return BigDecimal.ZERO;
        }
        double variance = window.stream()
                .map(ItemChartCandleDto::getClosePrice)
                .mapToDouble(value -> Math.pow(value.subtract(average).doubleValue(), 2))
                .average()
                .orElse(0);
        return BigDecimal.valueOf(Math.sqrt(variance)).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal atr(List<ItemChartCandleDto> candles, int count) {
        List<ItemChartCandleDto> window = tail(candles, count);
        if (window.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = window.stream()
                .map(candle -> candle.getHighPrice().subtract(candle.getLowPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(window.size()), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal stochasticK(List<ItemChartCandleDto> candles, int count) {
        List<ItemChartCandleDto> window = tail(candles, count);
        if (window.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal high = window.stream().map(ItemChartCandleDto::getHighPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal low = window.stream().map(ItemChartCandleDto::getLowPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal close = window.get(window.size() - 1).getClosePrice();
        if (high.compareTo(low) == 0) {
            return BigDecimal.ZERO;
        }
        return close.subtract(low).multiply(BigDecimal.valueOf(100)).divide(high.subtract(low), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal obv(List<ItemChartCandleDto> candles) {
        BigDecimal value = BigDecimal.ZERO;
        for (int i = 1; i < candles.size(); i++) {
            int compare = candles.get(i).getClosePrice().compareTo(candles.get(i - 1).getClosePrice());
            if (compare > 0) {
                value = value.add(BigDecimal.valueOf(candles.get(i).getVolume()));
            } else if (compare < 0) {
                value = value.subtract(BigDecimal.valueOf(candles.get(i).getVolume()));
            }
        }
        return value;
    }

    private List<ItemChartCandleDto> tail(List<ItemChartCandleDto> candles, int count) {
        if (candles.size() <= count) {
            return candles;
        }
        return candles.subList(candles.size() - count, candles.size());
    }
}
