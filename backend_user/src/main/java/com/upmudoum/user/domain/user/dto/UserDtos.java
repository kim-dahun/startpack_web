package com.upmudoum.user.domain.user.dto;

import com.upmudoum.user.domain.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public final class UserDtos {

    private UserDtos() {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRequest {
        private String comCd;
        private String userId;
        private String userName;
        private String password;
        private String email;
        private String phone;
        private String address;
        private String jobGradeId;
        private UserStatus status;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private String comCd;
        private String userId;
        private String userName;
        private String email;
        private String phone;
        private String address;
        private String jobGradeId;
        private String status;
    }
}
