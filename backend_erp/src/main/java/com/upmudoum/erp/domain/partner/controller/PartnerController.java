package com.upmudoum.erp.domain.partner.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.partner.dto.PartnerRequest;
import com.upmudoum.erp.domain.partner.dto.PartnerResponse;
import com.upmudoum.erp.domain.partner.service.PartnerService;
import com.upmudoum.erp.domain.partner.vo.PartnerStatus;
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
@RequestMapping("/api/erp/partners")
public class PartnerController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostMapping
    public ApiResponse<PartnerResponse> create(@Valid @RequestBody PartnerRequest request) {
        return ApiResponse.ok(partnerService.create(request));
    }

    @GetMapping
    public ApiResponse<List<PartnerResponse>> findAll() {
        return ApiResponse.ok(partnerService.findAll());
    }

    @GetMapping("/search")
    public ApiResponse<List<PartnerResponse>> search(@RequestParam(required = false) String partnerType,
                                                     @RequestParam(required = false) PartnerStatus status,
                                                     @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(partnerService.search(partnerType, status, keyword));
    }

    @PutMapping("/{id}")
    public ApiResponse<PartnerResponse> update(@PathVariable Long id, @Valid @RequestBody PartnerRequest request) {
        return ApiResponse.ok(partnerService.update(id, request));
    }
}
