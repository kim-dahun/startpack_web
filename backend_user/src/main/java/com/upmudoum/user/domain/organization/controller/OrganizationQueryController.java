package com.upmudoum.user.domain.organization.controller;

import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.organization.dto.OrganizationDtos.OrganizationUserResponse;
import com.upmudoum.user.domain.organization.service.OrganizationQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/organization")
public class OrganizationQueryController {

    private final OrganizationQueryService organizationQueryService;

    public OrganizationQueryController(OrganizationQueryService organizationQueryService) {
        this.organizationQueryService = organizationQueryService;
    }

    @GetMapping("/users")
    public ApiResponse<List<OrganizationUserResponse>> organizationUsers(
            @RequestParam String comCd,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.ok(organizationQueryService.organizationUsers(comCd, departmentId, keyword));
    }

    @GetMapping("/position-users")
    public ApiResponse<List<OrganizationUserResponse>> positionUsers(@RequestParam String comCd, @RequestParam String departmentId, @RequestParam String positionId) {
        return ApiResponse.ok(organizationQueryService.positionUsers(comCd, departmentId, positionId));
    }
}
