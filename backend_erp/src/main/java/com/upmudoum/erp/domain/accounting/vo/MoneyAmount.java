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
public class MoneyAmount {

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    private MoneyAmount(BigDecimal value) {
        this.value = value;
    }

    public static MoneyAmount of(BigDecimal value) {
        return new MoneyAmount(value);
    }
}
