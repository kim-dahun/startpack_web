package com.upmudoum.user.domain.menu.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionRequest;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionTreeResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuRequest;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuTreeResponse;
import com.upmudoum.user.domain.menu.service.MenuManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/menus")
public class MenuManagementController {

    private final MenuManagementService menuManagementService;

    public MenuManagementController(MenuManagementService menuManagementService) {
        this.menuManagementService = menuManagementService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<MenuTreeResponse>> menuTree(@RequestParam String comCd, @RequestParam String serviceId) {
        return ApiResponse.ok(menuManagementService.menuTree(comCd, serviceId));
    }

    @GetMapping
    public ApiResponse<List<MenuResponse>> menus(@RequestParam String comCd, @RequestParam String serviceId) {
        return ApiResponse.ok(menuManagementService.menus(comCd, serviceId));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> saveMenus(@RequestBody BulkRequestDto<MenuRequest> request) {
        return ApiResponse.ok(menuManagementService.saveMenus(request));
    }

    @GetMapping("/menu-permissions")
    public ApiResponse<List<MenuPermissionResponse>> permissions(@RequestParam String comCd, @RequestParam String serviceId, @RequestParam String groupId) {
        return ApiResponse.ok(menuManagementService.permissions(comCd, serviceId, groupId));
    }

    @GetMapping("/menu-permissions/tree")
    public ApiResponse<List<MenuPermissionTreeResponse>> permissionTree(@RequestParam String comCd, @RequestParam String serviceId, @RequestParam String groupId) {
        return ApiResponse.ok(menuManagementService.permissionTree(comCd, serviceId, groupId));
    }

    @PostMapping("/menu-permissions/bulk")
    public ApiResponse<BulkResultDto> savePermissions(@RequestBody BulkRequestDto<MenuPermissionRequest> request) {
        return ApiResponse.ok(menuManagementService.savePermissions(request));
    }
}
