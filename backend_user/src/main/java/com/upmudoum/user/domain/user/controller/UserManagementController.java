package com.upmudoum.user.domain.user.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.user.dto.UserDtos.UserRequest;
import com.upmudoum.user.domain.user.dto.UserDtos.UserResponse;
import com.upmudoum.user.domain.user.service.UserManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> users(@RequestParam String comCd) {
        return ApiResponse.ok(userManagementService.users(comCd));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> saveUsers(@RequestBody BulkRequestDto<UserRequest> request) {
        return ApiResponse.ok(userManagementService.saveUsers(request));
    }
}
