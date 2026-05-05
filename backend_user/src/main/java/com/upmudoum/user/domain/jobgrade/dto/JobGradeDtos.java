package com.upmudoum.user.domain.jobgrade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class JobGradeDtos {

    private JobGradeDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobGradeRequest {
        private String comCd;
        private String jobGradeId;
        private String jobGradeName;
        private String jobGradeType;
        private int sortSeq;
        private boolean enabled;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobGradeResponse {
        private String comCd;
        private String jobGradeId;
        private String jobGradeName;
        private String jobGradeType;
        private int sortSeq;
        private boolean enabled;
    }
}
