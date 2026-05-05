package com.upmudoum.trade.domain.item.controller;

import com.upmudoum.trade.domain.item.dto.ItemDto;
import com.upmudoum.trade.domain.item.dto.ItemChartCandleDto;
import com.upmudoum.trade.domain.item.dto.ItemIndicatorDto;
import com.upmudoum.trade.domain.item.dto.ItemMetricsDto;
import com.upmudoum.trade.domain.item.dto.ItemPriceDto;
import com.upmudoum.trade.domain.item.dto.ItemSearchResultDto;
import com.upmudoum.trade.domain.item.dto.ItemSummaryDto;
import com.upmudoum.trade.domain.item.dto.MultiQuoteRequest;
import com.upmudoum.trade.domain.item.dto.MultiOrderbookRequest;
import com.upmudoum.trade.domain.item.dto.OrderbookDto;
import com.upmudoum.trade.domain.item.service.ItemService;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> items(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.findItems(keyword, tradeMode);
    }

    @GetMapping("/search")
    public Map<String, ItemSearchResultDto> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.searchItems(keyword, tradeMode);
    }

    @GetMapping("/{itemCode}")
    public ItemDto item(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.findItem(itemCode, tradeMode);
    }

    @GetMapping("/{itemCode}/price")
    public ItemPriceDto price(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.findPrice(itemCode, tradeMode);
    }

    @GetMapping("/{itemCode}/quote")
    public ItemPriceDto quote(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.findPrice(itemCode, tradeMode);
    }

    @PostMapping("/quotes")
    public Map<String, ItemPriceDto> quotes(@RequestBody MultiQuoteRequest request) {
        return itemService.findQuotes(request);
    }

    @GetMapping("/{itemCode}/orderbook")
    public OrderbookDto orderbook(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.findOrderbook(itemCode, tradeMode);
    }

    @PostMapping("/orderbooks")
    public List<OrderbookDto> orderbooks(@RequestBody MultiOrderbookRequest request) {
        return itemService.findOrderbooks(request);
    }

    @GetMapping("/{itemCode}/chart")
    public List<ItemChartCandleDto> chart(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "DAY") String periodType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.findChart(itemCode, periodType, from, to, tradeMode);
    }

    @GetMapping("/{itemCode}/summary")
    public ItemSummaryDto summary(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.summary(itemCode, tradeMode);
    }

    @GetMapping("/{itemCode}/metrics")
    public ItemMetricsDto metrics(@PathVariable String itemCode) {
        return itemService.metrics(itemCode);
    }

    @GetMapping("/{itemCode}/indicators")
    public ItemIndicatorDto indicators(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "DAY") String periodType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "LIVE") KisTradeMode tradeMode
    ) {
        return itemService.indicators(itemCode, periodType, from, to, tradeMode);
    }
}
