package com.upmudoum.user.domain.group.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.group.UserGroup;
import com.upmudoum.user.domain.group.UserGroupMember;
import com.upmudoum.user.domain.group.UserGroupMemberRepository;
import com.upmudoum.user.domain.group.UserGroupRepository;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupMemberRequest;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupMemberResponse;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupRequest;
import com.upmudoum.user.domain.group.dto.GroupDtos.GroupResponse;
import com.upmudoum.user.domain.serviceaccess.ServiceId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupManagementService {

    private final UserGroupRepository groupRepository;
    private final UserGroupMemberRepository memberRepository;

    public GroupManagementService(UserGroupRepository groupRepository, UserGroupMemberRepository memberRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> groups(String comCd, String serviceId) {
        return groupRepository.findByComCdAndServiceIdOrderByGroupIdAsc(comCd, normalizeServiceId(serviceId)).stream().map(this::toGroup).toList();
    }

    @Transactional
    public BulkResultDto saveGroups(BulkRequestDto<GroupRequest> request) {
        request.getAdded().forEach(item -> {
            UserGroup group = new UserGroup(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId(), item.getGroupName());
            group.update(item.getGroupName(), item.getDescription(), item.isEnabled());
            groupRepository.save(group);
        });
        request.getUpdated().forEach(item -> groupRepository.findByComCdAndServiceIdAndGroupId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Group was not found."))
                .update(item.getGroupName(), item.getDescription(), item.isEnabled()));
        request.getDeleted().forEach(item -> groupRepository.deleteByComCdAndServiceIdAndGroupId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> members(String comCd, String serviceId, String groupId) {
        return memberRepository.findByComCdAndServiceIdAndGroupId(comCd, normalizeServiceId(serviceId), groupId).stream().map(this::toMember).toList();
    }

    @Transactional
    public BulkResultDto saveMembers(BulkRequestDto<GroupMemberRequest> request) {
        request.getAdded().forEach(item -> memberRepository.save(new UserGroupMember(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId(), item.getUserId())));
        request.getDeleted().forEach(item -> memberRepository.deleteByComCdAndServiceIdAndGroupIdAndUserId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId(), item.getUserId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private String normalizeServiceId(String value) {
        try {
            return ServiceId.from(value).name();
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Unsupported serviceId.");
        }
    }

    private GroupResponse toGroup(UserGroup group) {
        return new GroupResponse(group.getComCd(), group.getServiceId(), group.getGroupId(), group.getGroupName(), group.getDescription(), group.isEnabled());
    }

    private GroupMemberResponse toMember(UserGroupMember member) {
        return new GroupMemberResponse(member.getComCd(), member.getServiceId(), member.getGroupId(), member.getUserId());
    }
}
