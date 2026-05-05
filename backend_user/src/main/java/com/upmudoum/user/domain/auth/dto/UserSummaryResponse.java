package com.upmudoum.user.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private String comCd;
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String address;
    private String status;
}
