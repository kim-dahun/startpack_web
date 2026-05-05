package com.upmudoum.user.domain.position.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.position.dto.PositionDtos.PositionRequest;
import com.upmudoum.user.domain.position.dto.PositionDtos.PositionResponse;
import com.upmudoum.user.domain.position.service.PositionManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/positions")
public class PositionManagementController {

    private final PositionManagementService positionManagementService;

    public PositionManagementController(PositionManagementService positionManagementService) {
        this.positionManagementService = positionManagementService;
    }

    @GetMapping
    public ApiResponse<List<PositionResponse>> positions(@RequestParam String comCd) {
        return ApiResponse.ok(positionManagementService.positions(comCd));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> savePositions(@RequestBody BulkRequestDto<PositionRequest> request) {
        return ApiResponse.ok(positionManagementService.savePositions(request));
    }
}
