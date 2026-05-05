package com.upmudoum.user.domain.dropdown.controller;

import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.dropdown.dto.DropdownOptionResponse;
import com.upmudoum.user.domain.dropdown.service.DropdownService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/dropdown")
public class DropdownController {

    private final DropdownService dropdownService;

    public DropdownController(DropdownService dropdownService) {
        this.dropdownService = dropdownService;
    }

    @GetMapping("/departments")
    public ApiResponse<List<DropdownOptionResponse>> getDepartments(@RequestParam String comCd) {
        return ApiResponse.ok(dropdownService.getDepartments(comCd));
    }

    @GetMapping("/job-grades")
    public ApiResponse<List<DropdownOptionResponse>> getJobGrades(@RequestParam String comCd) {
        return ApiResponse.ok(dropdownService.getJobGrades(comCd));
    }

    @GetMapping("/positions")
    public ApiResponse<List<DropdownOptionResponse>> getPositions(@RequestParam String comCd) {
        return ApiResponse.ok(dropdownService.getPositions(comCd));
    }

    @GetMapping("/users")
    public ApiResponse<List<DropdownOptionResponse>> getUserList(@RequestParam String comCd) {
        return ApiResponse.ok(dropdownService.getUserList(comCd));
    }

    @GetMapping("/codes")
    public ApiResponse<List<DropdownOptionResponse>> getCodeList(
            @RequestParam String comCd,
            @RequestParam String serviceId,
            @RequestParam String codeGroupId
    ) {
        return ApiResponse.ok(dropdownService.getCodeList(comCd, serviceId, codeGroupId));
    }
}
