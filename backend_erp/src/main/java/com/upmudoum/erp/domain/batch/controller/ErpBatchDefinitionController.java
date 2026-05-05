package com.upmudoum.erp.domain.batch.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.batch.dto.ErpBatchDefinitionRequest;
import com.upmudoum.erp.domain.batch.dto.ErpBatchDefinitionResponse;
import com.upmudoum.erp.domain.batch.service.ErpBatchDefinitionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/batch-definitions")
public class ErpBatchDefinitionController {

    private final ErpBatchDefinitionService batchDefinitionService;

    public ErpBatchDefinitionController(ErpBatchDefinitionService batchDefinitionService) {
        this.batchDefinitionService = batchDefinitionService;
    }

    @PostMapping
    public ApiResponse<ErpBatchDefinitionResponse> save(@Valid @RequestBody ErpBatchDefinitionRequest request) {
        return ApiResponse.ok(batchDefinitionService.save(request));
    }

    @GetMapping
    public ApiResponse<List<ErpBatchDefinitionResponse>> findAll() {
        return ApiResponse.ok(batchDefinitionService.findAll());
    }
}
