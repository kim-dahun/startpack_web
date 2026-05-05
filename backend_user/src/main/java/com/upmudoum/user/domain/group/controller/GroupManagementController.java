package com.upmudoum.user.domain.group.controller;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.response.ApiResponse;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupMemberRequest;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupMemberResponse;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupRequest;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupResponse;
import com.upmudoum.user.domain.group.service.GroupManagementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/groups")
public class GroupManagementController {

    private final GroupManagementService groupManagementService;

    public GroupManagementController(GroupManagementService groupManagementService) {
        this.groupManagementService = groupManagementService;
    }

    @GetMapping
    public ApiResponse<List<GroupResponse>> groups(@RequestParam String comCd, @RequestParam String serviceId) {
        return ApiResponse.ok(groupManagementService.groups(comCd, serviceId));
    }

    @PostMapping("/bulk")
    public ApiResponse<BulkResultDto> saveGroups(@RequestBody BulkRequestDto<GroupRequest> request) {
        return ApiResponse.ok(groupManagementService.saveGroups(request));
    }

    @GetMapping("/group-members")
    public ApiResponse<List<GroupMemberResponse>> members(@RequestParam String comCd, @RequestParam String serviceId, @RequestParam String groupId) {
        return ApiResponse.ok(groupManagementService.members(comCd, serviceId, groupId));
    }

    @PostMapping("/group-members/bulk")
    public ApiResponse<BulkResultDto> saveMembers(@RequestBody BulkRequestDto<GroupMemberRequest> request) {
        return ApiResponse.ok(groupManagementService.saveMembers(request));
    }
}
