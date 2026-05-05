package com.upmudoum.erp.domain.item.vo;

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
public class ItemCode {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String value;

    private ItemCode(String value) {
        this.value = value;
    }

    public static ItemCode of(String value) {
        return new ItemCode(value);
    }
}
