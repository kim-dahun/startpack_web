package com.upmudoum.user.domain.serviceaccess.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class ServiceAccessDtos {

    private ServiceAccessDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceAccessRequest {
        private String comCd;
        private String userId;
        private String serviceId;
        private boolean accessible;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceAccessResponse {
        private String comCd;
        private String userId;
        private String serviceId;
        private boolean accessible;
    }
}
