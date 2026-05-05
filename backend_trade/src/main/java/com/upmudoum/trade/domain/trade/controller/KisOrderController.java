package com.upmudoum.trade.domain.trade.controller;

import com.upmudoum.trade.domain.trade.dto.OrderableAmountDto;
import com.upmudoum.trade.domain.trade.dto.OrderableAmountRequest;
import com.upmudoum.trade.domain.trade.dto.PlaceOrderRequest;
import com.upmudoum.trade.domain.trade.dto.PlaceOrderResultDto;
import com.upmudoum.trade.domain.trade.service.KisOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/kis/orders")
public class KisOrderController {

    private final KisOrderService kisOrderService;

    public KisOrderController(KisOrderService kisOrderService) {
        this.kisOrderService = kisOrderService;
    }

    @PostMapping("/orderable-amount")
    public OrderableAmountDto orderableAmount(@Valid @RequestBody OrderableAmountRequest request) {
        return kisOrderService.orderableAmount(request);
    }

    @PostMapping("/cash")
    public PlaceOrderResultDto cashOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return kisOrderService.placeOrder(request);
    }
}
