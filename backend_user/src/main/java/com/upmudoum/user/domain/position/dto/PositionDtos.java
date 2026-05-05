package com.upmudoum.user.domain.position.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class PositionDtos {

    private PositionDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionRequest {
        private String comCd;
        private String positionId;
        private String positionName;
        private String positionType;
        private int sortSeq;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionResponse {
        private String comCd;
        private String positionId;
        private String positionName;
        private String positionType;
        private int sortSeq;
        private boolean enabled;
    }
}
