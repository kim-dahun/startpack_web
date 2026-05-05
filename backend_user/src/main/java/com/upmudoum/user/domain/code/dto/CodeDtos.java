package com.upmudoum.user.domain.code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class CodeDtos {

    private CodeDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeGroupRequest {
        private String comCd;
        private String serviceId;
        private String codeGroupId;
        private String codeGroupName;
        private String description;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeGroupResponse {
        private String comCd;
        private String serviceId;
        private String codeGroupId;
        private String codeGroupName;
        private String description;
        private boolean enabled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeRequest {
        private String comCd;
        private String serviceId;
        private String codeGroupId;
        private String codeId;
        private String codeName;
        private String parentCodeGroupId;
        private String parentCodeId;
        private String subInfo1;
        private String subInfo2;
        private String subInfo3;
        private int sortSeq;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeResponse {
        private String comCd;
        private String serviceId;
        private String codeGroupId;
        private String codeId;
        private String codeName;
        private String parentCodeGroupId;
        private String parentCodeId;
        private String subInfo1;
        private String subInfo2;
        private String subInfo3;
        private int sortSeq;
        private boolean enabled;
    }
}
