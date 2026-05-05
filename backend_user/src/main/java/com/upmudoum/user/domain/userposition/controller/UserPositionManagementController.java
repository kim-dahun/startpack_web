package com.upmudoum.user.domain.userposition.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionPrimaryYnRequest;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionRequest;
import com.upmudoum.user.domain.userposition.dto.UserPositionDtos.UserPositionResponse;
import com.upmudoum.user.domain.userposition.service.UserPositionManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/user-positions")
public class UserPositionManagementController {

    private final UserPositionManagementService userPositionManagementService;

    public UserPositionManagementController(UserPositionManagementService userPositionManagementService) {
        this.userPositionManagementService = userPositionManagementService;
    }

    @GetMapping
    public ApiResponse<List<UserPositionResponse>> userPositions(@RequestParam String comCd, @RequestParam String userId) {
        return ApiResponse.ok(userPositionManagementService.userPositions(comCd, userId));
    }

    @GetMapping("/department-members")
    public ApiResponse<List<UserPositionResponse>> departmentMembers(@RequestParam String comCd, @RequestParam String departmentId) {
        return ApiResponse.ok(userPositionManagementService.departmentMembers(comCd, departmentId));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> saveUserPositions(@RequestBody BulkRequestDto<UserPositionRequest> request) {
        return ApiResponse.ok(userPositionManagementService.saveUserPositions(request));
    }

    @PostMapping("/department-members/bulk")
    public ApiResponse<BulkResultDto> saveDepartmentMembers(@RequestBody BulkRequestDto<UserPositionRequest> request) {
        return ApiResponse.ok(userPositionManagementService.saveDepartmentMembers(request));
    }

    @PostMapping("/primary-yn")
    public ApiResponse<UserPositionResponse> updatePrimaryYn(@RequestBody UserPositionPrimaryYnRequest request) {
        return ApiResponse.ok(userPositionManagementService.updatePrimaryYn(request));
    }
}
