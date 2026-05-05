package com.upmudoum.user.domain.userposition.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class UserPositionDtos {

    private UserPositionDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPositionRequest {
        private String comCd;
        private String userPositionId;
        private String userId;
        private String departmentId;
        private String positionId;
        private boolean primaryYn;
        private boolean enabled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPositionPrimaryYnRequest {
        private String comCd;
        private String userId;
        private String departmentId;
        private String positionId;
        private boolean primaryYn;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPositionResponse {
        private String comCd;
        private String userPositionId;
        private String userId;
        private String departmentId;
        private String positionId;
        private boolean primaryYn;
        private boolean enabled;
    }
}
