package com.upmudoum.erp.domain.item.vo;

public enum ItemType {
    RAW_MATERIAL,
    SEMI_FINISHED,
    FINISHED_GOOD,
    CONSUMABLE;

    public boolean isProducible() {
        return this == SEMI_FINISHED || this == FINISHED_GOOD;
    }
}
