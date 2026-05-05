package com.upmudoum.auth.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginGroupResponse {

    private final String comCd;
    private final String serviceId;
    private final String groupId;
    private final String groupName;
}
