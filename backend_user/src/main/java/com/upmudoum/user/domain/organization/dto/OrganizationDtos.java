package com.upmudoum.user.domain.organization.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public final class OrganizationDtos {

    private OrganizationDtos() {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAffiliationResponse {
        private String comCd;
        private String userPositionId;
        private String departmentId;
        private String departmentName;
        private String positionId;
        private String positionName;
        private boolean primaryYn;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationUserResponse {
        private String comCd;
        private String userId;
        private String userName;
        private String jobGradeId;
        private String jobGradeName;
        private List<UserAffiliationResponse> affiliations;
    }
}
