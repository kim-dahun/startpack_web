package com.upmudoum.user.domain.department.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class DepartmentDtos {

    private DepartmentDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentRequest {
        private String comCd;
        private String departmentId;
        private String departmentName;
        private String parentDepartmentId;
        private String departmentHeadUserId;
        private String departmentHeadPositionId;
        private int sortSeq;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentResponse {
        private String comCd;
        private String departmentId;
        private String departmentName;
        private String parentDepartmentId;
        private String departmentHeadUserId;
        private String departmentHeadPositionId;
        private int sortSeq;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentTreeResponse {
        private String comCd;
        private String departmentId;
        private String departmentName;
        private String parentDepartmentId;
        private String departmentHeadUserId;
        private String departmentHeadPositionId;
        private int sortSeq;
        private boolean enabled;
        private List<DepartmentTreeResponse> children;
    }
}
