package com.upmudoum.erp.domain.process.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.process.dto.ErpProcessRequest;
import com.upmudoum.erp.domain.process.dto.ErpProcessResponse;
import com.upmudoum.erp.domain.process.service.ErpProcessService;
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
@RequestMapping("/api/erp/processes")
public class ErpProcessController {

    private final ErpProcessService processService;

    public ErpProcessController(ErpProcessService processService) {
        this.processService = processService;
    }

    @PostMapping
    public ApiResponse<ErpProcessResponse> create(@Valid @RequestBody ErpProcessRequest request) {
        return ApiResponse.ok(processService.create(request));
    }

    @GetMapping
    public ApiResponse<List<ErpProcessResponse>> findAll() {
        return ApiResponse.ok(processService.findAll());
    }

    @GetMapping("/search")
    public ApiResponse<List<ErpProcessResponse>> search(@RequestParam(required = false) String processType) {
        return ApiResponse.ok(processService.search(processType));
    }

    @PutMapping("/{id}")
    public ApiResponse<ErpProcessResponse> update(@PathVariable Long id, @Valid @RequestBody ErpProcessRequest request) {
        return ApiResponse.ok(processService.update(id, request));
    }
}
