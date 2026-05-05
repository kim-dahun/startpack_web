package com.upmudoum.trade.domain.item.controller;

import com.upmudoum.trade.domain.item.dto.FrequentSearchItemDto;
import com.upmudoum.trade.domain.item.dto.RecordFrequentSearchRequest;
import com.upmudoum.trade.domain.item.service.FrequentSearchItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/items/frequent-searches")
public class FrequentSearchItemController {

    private final FrequentSearchItemService service;

    public FrequentSearchItemController(FrequentSearchItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<FrequentSearchItemDto> frequentSearches(@RequestParam String userId) {
        return service.findByUserId(userId);
    }

    @PostMapping
    public FrequentSearchItemDto record(@Valid @RequestBody RecordFrequentSearchRequest request) {
        return service.record(request);
    }
}
