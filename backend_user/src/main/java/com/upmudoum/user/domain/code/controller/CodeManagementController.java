package com.upmudoum.user.domain.code.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeGroupRequest;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeGroupResponse;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeRequest;
import com.upmudoum.user.domain.code.dto.CodeDtos.CodeResponse;
import com.upmudoum.user.domain.code.service.CodeManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class CodeManagementController {

    private final CodeManagementService codeManagementService;

    public CodeManagementController(CodeManagementService codeManagementService) {
        this.codeManagementService = codeManagementService;
    }

    @GetMapping("/code-groups")
    public ApiResponse<List<CodeGroupResponse>> codeGroups(@RequestParam String comCd, @RequestParam String serviceId) {
        return ApiResponse.ok(codeManagementService.codeGroups(comCd, serviceId));
    }

    @PostMapping("/code-groups/bulk")
    public ApiResponse<BulkResultDto> saveCodeGroups(@RequestBody BulkRequestDto<CodeGroupRequest> request) {
        return ApiResponse.ok(codeManagementService.saveCodeGroups(request));
    }

    @GetMapping("/codes")
    public ApiResponse<List<CodeResponse>> codes(@RequestParam String comCd, @RequestParam String serviceId, @RequestParam String codeGroupId) {
        return ApiResponse.ok(codeManagementService.codes(comCd, serviceId, codeGroupId));
    }

    @PostMapping("/codes/bulk")
    public ApiResponse<BulkResultDto> saveCodes(@RequestBody BulkRequestDto<CodeRequest> request) {
        return ApiResponse.ok(codeManagementService.saveCodes(request));
    }
}
