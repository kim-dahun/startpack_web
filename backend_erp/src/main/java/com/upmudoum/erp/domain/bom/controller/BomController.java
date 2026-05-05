package com.upmudoum.erp.domain.bom.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.bom.dto.BomComponentRequest;
import com.upmudoum.erp.domain.bom.dto.BomComponentResponse;
import com.upmudoum.erp.domain.bom.dto.BomVersionRequest;
import com.upmudoum.erp.domain.bom.dto.BomVersionResponse;
import com.upmudoum.erp.domain.bom.service.BomService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/boms")
public class BomController {

    private final BomService bomService;

    public BomController(BomService bomService) {
        this.bomService = bomService;
    }

    @PostMapping("/versions")
    public ApiResponse<BomVersionResponse> createVersion(@Valid @RequestBody BomVersionRequest request) {
        return ApiResponse.ok(BomVersionResponse.from(bomService.createVersion(
                request.getParentItemId(), request.getVersionNo(), request.getEffectiveFrom(), request.getEffectiveTo())));
    }

    @GetMapping("/versions")
    public ApiResponse<List<BomVersionResponse>> findVersions(@RequestParam Long parentItemId) {
        return ApiResponse.ok(bomService.findVersions(parentItemId));
    }

    @PostMapping("/versions/{bomVersionId}/components")
    public ApiResponse<BomComponentResponse> addComponent(@PathVariable Long bomVersionId,
                                                          @Valid @RequestBody BomComponentRequest request) {
        return ApiResponse.ok(BomComponentResponse.from(bomService.addComponent(
                bomVersionId, request.getComponentItemId(), request.getRequiredQuantity(), request.getLossRate())));
    }

    @GetMapping("/versions/{bomVersionId}/components")
    public ApiResponse<List<BomComponentResponse>> findComponents(@PathVariable Long bomVersionId) {
        return ApiResponse.ok(bomService.findComponents(bomVersionId));
    }

    @PutMapping("/versions/{bomVersionId}/default")
    public ApiResponse<BomVersionResponse> changeDefaultVersion(@PathVariable Long bomVersionId) {
        return ApiResponse.ok(bomService.changeDefaultVersion(bomVersionId));
    }
}
