package com.upmudoum.erp.domain.warehouse.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WarehouseCode {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String value;

    private WarehouseCode(String value) {
        this.value = value;
    }

    public static WarehouseCode of(String value) {
        return new WarehouseCode(value);
    }
}
