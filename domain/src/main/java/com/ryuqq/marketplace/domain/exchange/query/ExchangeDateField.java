package com.ryuqq.marketplace.domain.exchange.query;

import com.ryuqq.marketplace.domain.common.vo.DateField;

/** 교환 날짜 필드. */
public enum ExchangeDateField implements DateField {
    REQUESTED("requestedAt"),
    COMPLETED("completedAt");

    private final String fieldName;

    ExchangeDateField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static ExchangeDateField defaultField() {
        return REQUESTED;
    }

    public static ExchangeDateField fromString(String value) {
        if (value == null || value.isBlank()) { return null; }
        return valueOf(value);
    }
}
