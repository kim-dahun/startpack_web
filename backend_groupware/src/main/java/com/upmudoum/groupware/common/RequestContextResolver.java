package com.upmudoum.groupware.common;

import org.springframework.stereotype.Component;

import com.upmudoum.groupware.common.vo.TenantKey;

@Component
public class RequestContextResolver {

    public static final String COM_CD_HEADER = "X-Com-Cd";
    public static final String USER_ID_HEADER = "X-User-Id";

    public TenantKey resolve(String comCd, String userId) {
        if (comCd == null || comCd.isBlank()) {
            throw new GroupwareException(GroupwareErrorCode.REQUIRED_HEADER_MISSING, COM_CD_HEADER + " header is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new GroupwareException(GroupwareErrorCode.REQUIRED_HEADER_MISSING, USER_ID_HEADER + " header is required");
        }
        return new TenantKey(comCd, userId);
    }
}
