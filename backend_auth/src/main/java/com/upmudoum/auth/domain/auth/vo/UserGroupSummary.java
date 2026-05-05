package com.upmudoum.auth.domain.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserGroupSummary {

    private final String comCd;
    private final String serviceId;
    private final String groupId;
    private final String groupName;
}
