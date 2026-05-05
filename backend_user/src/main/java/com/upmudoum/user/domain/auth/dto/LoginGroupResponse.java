package com.upmudoum.user.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginGroupResponse {

    private String comCd;
    private String serviceId;
    private String groupId;
    private String groupName;
}
