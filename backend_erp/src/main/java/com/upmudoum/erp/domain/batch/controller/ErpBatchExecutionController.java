package com.upmudoum.erp.domain.batch.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.batch.dto.ErpBatchExecutionRequest;
import com.upmudoum.erp.domain.batch.dto.ErpBatchExecutionResponse;
import com.upmudoum.erp.domain.batch.service.ErpBatchExecutionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/erp/batch-executions")
public class ErpBatchExecutionController {

    private final ErpBatchExecutionService executionService;

    public ErpBatchExecutionController(ErpBatchExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping
    public ApiResponse<ErpBatchExecutionResponse> run(@Valid @RequestBody ErpBatchExecutionRequest request) {
        return ApiResponse.ok(executionService.run(request));
    }

    @GetMapping
    public ApiResponse<List<ErpBatchExecutionResponse>> findExecutions(@RequestParam String jobName) {
        return ApiResponse.ok(executionService.findExecutions(jobName));
    }

    @GetMapping("/{executionId}")
    public ApiResponse<ErpBatchExecutionResponse> findExecution(@PathVariable Long executionId) {
        return ApiResponse.ok(executionService.findExecution(executionId));
    }
}
