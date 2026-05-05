package com.upmudoum.erp.domain.accounting.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UnitPrice {

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal value;

    private UnitPrice(BigDecimal value) {
        this.value = value;
    }

    public static UnitPrice of(BigDecimal value) {
        return new UnitPrice(value);
    }
}
