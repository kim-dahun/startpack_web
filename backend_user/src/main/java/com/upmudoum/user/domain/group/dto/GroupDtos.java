package com.upmudoum.user.domain.group.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class GroupDtos {

    private GroupDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupRequest {
        private String comCd;
        private String serviceId;
        private String groupId;
        private String groupName;
        private String description;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupResponse {
        private String comCd;
        private String serviceId;
        private String groupId;
        private String groupName;
        private String description;
        private boolean enabled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMemberRequest {
        private String comCd;
        private String serviceId;
        private String groupId;
        private String userId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMemberResponse {
        private String comCd;
        private String serviceId;
        private String groupId;
        private String userId;
    }
}
