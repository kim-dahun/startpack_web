package com.upmudoum.user.domain.serviceaccess;

public enum ServiceId {
    ERP,
    GROUPWARE,
    TRADE;

    public static ServiceId from(String value) {
        return ServiceId.valueOf(value.toUpperCase());
    }
}
