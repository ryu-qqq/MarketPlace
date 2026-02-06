package com.ryuqq.marketplace.domain.seller.vo;

import java.math.BigDecimal;

/**
 * 수수료율 Value Object.
 *
 * <p>0 ~ 100 사이의 퍼센트 값을 표현합니다.
 */
public record CommissionRate(BigDecimal value) {

    private static final BigDecimal MIN_RATE = BigDecimal.ZERO;
    private static final BigDecimal MAX_RATE = new BigDecimal("100");

    public CommissionRate {
        if (value == null) {
            throw new IllegalArgumentException("수수료율은 필수입니다");
        }
        if (value.compareTo(MIN_RATE) < 0 || value.compareTo(MAX_RATE) > 0) {
            throw new IllegalArgumentException("수수료율은 0 ~ 100 사이여야 합니다: " + value);
        }
    }

    public static CommissionRate of(BigDecimal value) {
        return new CommissionRate(value);
    }

    public static CommissionRate of(double value) {
        return new CommissionRate(BigDecimal.valueOf(value));
    }

    public static CommissionRate zero() {
        return new CommissionRate(BigDecimal.ZERO);
    }

    public double doubleValue() {
        return value.doubleValue();
    }
}
