package com.upmudoum.erp.domain.accounting.controller;

import com.upmudoum.erp.common.response.ApiResponse;
import com.upmudoum.erp.domain.accounting.dto.AccountingVoucherRequest;
import com.upmudoum.erp.domain.accounting.dto.AccountingVoucherResponse;
import com.upmudoum.erp.domain.accounting.service.AccountingEventService;
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
@RequestMapping("/api/erp/accounting/vouchers")
public class AccountingVoucherController {

    private final AccountingEventService accountingEventService;

    public AccountingVoucherController(AccountingEventService accountingEventService) {
        this.accountingEventService = accountingEventService;
    }

    @PostMapping
    public ApiResponse<AccountingVoucherResponse> create(@Valid @RequestBody AccountingVoucherRequest request) {
        return ApiResponse.ok(accountingEventService.create(request));
    }

    @PostMapping("/{id}/post")
    public ApiResponse<AccountingVoucherResponse> post(@PathVariable Long id) {
        return ApiResponse.ok(accountingEventService.post(id));
    }

    @GetMapping
    public ApiResponse<List<AccountingVoucherResponse>> findBySource(@RequestParam String sourceEventType,
                                                                     @RequestParam String sourceEventId) {
        return ApiResponse.ok(accountingEventService.findBySource(sourceEventType, sourceEventId));
    }
}
