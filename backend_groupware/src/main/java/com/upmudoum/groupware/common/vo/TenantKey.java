package com.upmudoum.groupware.common.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TenantKey {

    private String comCd;
    private String userId;

    public TenantKey(String comCd, String userId) {
        if (comCd == null || comCd.isBlank()) {
            throw new IllegalArgumentException("comCd is required");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        this.comCd = comCd;
        this.userId = userId;
    }

    public boolean sameCompany(TenantKey other) {
        return other != null && comCd.equals(other.getComCd());
    }
}
