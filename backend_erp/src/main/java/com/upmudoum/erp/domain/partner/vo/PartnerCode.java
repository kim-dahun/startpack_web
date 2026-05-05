package com.upmudoum.erp.domain.partner.vo;

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
public class PartnerCode {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String value;

    private PartnerCode(String value) {
        this.value = value;
    }

    public static PartnerCode of(String value) {
        return new PartnerCode(value);
    }
}
