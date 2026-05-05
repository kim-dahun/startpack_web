package com.upmudoum.trade.domain.trade.controller;

import com.upmudoum.trade.domain.trade.dto.OrderValidationRequest;
import com.upmudoum.trade.domain.trade.dto.OrderValidationResultDto;
import com.upmudoum.trade.domain.trade.service.OrderValidationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/orders")
public class OrderValidationController {

    private final OrderValidationService orderValidationService;

    public OrderValidationController(OrderValidationService orderValidationService) {
        this.orderValidationService = orderValidationService;
    }

    @PostMapping("/validate")
    public OrderValidationResultDto validate(@Valid @RequestBody OrderValidationRequest request) {
        return orderValidationService.validate(request);
    }
}
