package com.upmudoum.erp.domain.inventory.vo;

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
public class Quantity {

    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal value = BigDecimal.ZERO;

    private Quantity(BigDecimal value) {
        this.value = value;
    }

    public static Quantity zero() {
        return new Quantity(BigDecimal.ZERO);
    }

    public static Quantity of(BigDecimal value) {
        return new Quantity(value);
    }

    public Quantity add(Quantity quantity) {
        return new Quantity(this.value.add(quantity.value));
    }

    public Quantity subtract(Quantity quantity) {
        return new Quantity(this.value.subtract(quantity.value));
    }

    public Quantity negate() {
        return new Quantity(this.value.negate());
    }

    public boolean isNegative() {
        return value.signum() < 0;
    }

    public boolean isPositive() {
        return value.signum() > 0;
    }

    public boolean isGreaterThanOrEqualTo(Quantity quantity) {
        return value.compareTo(quantity.value) >= 0;
    }
}
