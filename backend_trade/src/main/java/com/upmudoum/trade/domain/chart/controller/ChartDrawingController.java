package com.upmudoum.trade.domain.chart.controller;

import com.upmudoum.trade.domain.chart.dto.ChartDrawingDto;
import com.upmudoum.trade.domain.chart.dto.SaveChartDrawingRequest;
import com.upmudoum.trade.domain.chart.service.ChartDrawingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/items/{itemCode}/drawings")
public class ChartDrawingController {

    private final ChartDrawingService service;

    public ChartDrawingController(ChartDrawingService service) {
        this.service = service;
    }

    @GetMapping
    public List<ChartDrawingDto> drawings(@PathVariable String itemCode, @RequestParam String userId) {
        return service.find(userId, itemCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChartDrawingDto create(
            @PathVariable String itemCode,
            @RequestParam String userId,
            @Valid @RequestBody SaveChartDrawingRequest request
    ) {
        return service.create(userId, itemCode, request);
    }

    @PatchMapping("/{drawingId}")
    public ChartDrawingDto update(
            @PathVariable String itemCode,
            @PathVariable Long drawingId,
            @RequestParam String userId,
            @Valid @RequestBody SaveChartDrawingRequest request
    ) {
        return service.update(userId, itemCode, drawingId, request);
    }

    @DeleteMapping("/{drawingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String itemCode, @PathVariable Long drawingId, @RequestParam String userId) {
        service.delete(userId, itemCode, drawingId);
    }
}
