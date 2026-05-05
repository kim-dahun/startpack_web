package com.upmudoum.trade.domain.trade.controller;

import com.upmudoum.trade.domain.trade.dto.LivePlaceOrderRequest;
import com.upmudoum.trade.domain.trade.dto.PlaceOrderResultDto;
import com.upmudoum.trade.domain.trade.service.KisOrderService;
import com.upmudoum.trade.domain.trade.vo.TradeSide;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/orders")
public class LiveOrderController {

    private final KisOrderService kisOrderService;

    public LiveOrderController(KisOrderService kisOrderService) {
        this.kisOrderService = kisOrderService;
    }

    @PostMapping("/buy")
    public PlaceOrderResultDto buy(@Valid @RequestBody LivePlaceOrderRequest request) {
        return kisOrderService.placeOrder(request.toPlaceOrderRequest(TradeSide.BUY));
    }

    @PostMapping("/sell")
    public PlaceOrderResultDto sell(@Valid @RequestBody LivePlaceOrderRequest request) {
        return kisOrderService.placeOrder(request.toPlaceOrderRequest(TradeSide.SELL));
    }
}
