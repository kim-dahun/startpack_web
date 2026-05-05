package com.upmudoum.user.domain.serviceaccess.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.serviceaccess.dto.ServiceAccessDtos.ServiceAccessRequest;
import com.upmudoum.user.domain.serviceaccess.dto.ServiceAccessDtos.ServiceAccessResponse;
import com.upmudoum.user.domain.serviceaccess.service.ServiceAccessManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/service-accesses")
public class ServiceAccessManagementController {

    private final ServiceAccessManagementService serviceAccessManagementService;

    public ServiceAccessManagementController(ServiceAccessManagementService serviceAccessManagementService) {
        this.serviceAccessManagementService = serviceAccessManagementService;
    }

    @GetMapping
    public ApiResponse<List<ServiceAccessResponse>> serviceAccesses(@RequestParam String comCd, @RequestParam String userId) {
        return ApiResponse.ok(serviceAccessManagementService.serviceAccesses(comCd, userId));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> saveServiceAccesses(@RequestBody BulkRequestDto<ServiceAccessRequest> request) {
        return ApiResponse.ok(serviceAccessManagementService.saveServiceAccesses(request));
    }
}
