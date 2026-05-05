package com.upmudoum.erp.domain.route.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.route.dto.RouteRequest;
import com.upmudoum.erp.domain.route.dto.RouteResponse;
import com.upmudoum.erp.domain.route.dto.RouteStepRequest;
import com.upmudoum.erp.domain.route.dto.RouteStepResponse;
import com.upmudoum.erp.domain.route.service.RouteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ApiResponse<RouteResponse> create(@Valid @RequestBody RouteRequest request) {
        return ApiResponse.ok(routeService.create(request));
    }

    @GetMapping
    public ApiResponse<List<RouteResponse>> findAll() {
        return ApiResponse.ok(routeService.findAll());
    }

    @GetMapping("/search")
    public ApiResponse<List<RouteResponse>> search(@RequestParam(required = false) Long itemId) {
        return ApiResponse.ok(routeService.search(itemId));
    }

    @PutMapping("/{routeId}")
    public ApiResponse<RouteResponse> update(@PathVariable Long routeId, @Valid @RequestBody RouteRequest request) {
        return ApiResponse.ok(routeService.update(routeId, request));
    }

    @PostMapping("/{routeId}/steps")
    public ApiResponse<RouteStepResponse> addStep(@PathVariable Long routeId,
                                                  @Valid @RequestBody RouteStepRequest request) {
        return ApiResponse.ok(routeService.addStep(routeId, request));
    }

    @PutMapping("/{routeId}/steps/{routeStepId}")
    public ApiResponse<RouteStepResponse> updateStep(@PathVariable Long routeId, @PathVariable Long routeStepId,
                                                     @Valid @RequestBody RouteStepRequest request) {
        return ApiResponse.ok(routeService.updateStep(routeId, routeStepId, request));
    }

    @DeleteMapping("/{routeId}/steps/{routeStepId}")
    public ApiResponse<Void> disableStep(@PathVariable Long routeId, @PathVariable Long routeStepId) {
        routeService.disableStep(routeId, routeStepId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{routeId}/steps")
    public ApiResponse<List<RouteStepResponse>> findSteps(@PathVariable Long routeId) {
        return ApiResponse.ok(routeService.findSteps(routeId));
    }
}
