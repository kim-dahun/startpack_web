package com.upmudoum.user.domain.department.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentRequest;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentResponse;
import com.upmudoum.user.domain.department.dto.DepartmentDtos.DepartmentTreeResponse;
import com.upmudoum.user.domain.department.service.DepartmentManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/departments")
public class DepartmentManagementController {

    private final DepartmentManagementService departmentManagementService;

    public DepartmentManagementController(DepartmentManagementService departmentManagementService) {
        this.departmentManagementService = departmentManagementService;
    }

    @GetMapping
    public ApiResponse<List<DepartmentResponse>> departments(@RequestParam String comCd) {
        return ApiResponse.ok(departmentManagementService.departments(comCd));
    }

    @GetMapping("/tree")
    public ApiResponse<List<DepartmentTreeResponse>> departmentTree(@RequestParam String comCd) {
        return ApiResponse.ok(departmentManagementService.departmentTree(comCd));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> saveDepartments(@RequestBody BulkRequestDto<DepartmentRequest> request) {
        return ApiResponse.ok(departmentManagementService.saveDepartments(request));
    }
}
