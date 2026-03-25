package com.ryuqq.marketplace.domain.exchange.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 교환 정렬 키. */
public enum ExchangeSortKey implements SortKey {
    REQUESTED_AT("requestedAt"),
    COMPLETED_AT("completedAt"),
    CREATED_AT("createdAt");

    private final String fieldName;

    ExchangeSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static ExchangeSortKey defaultKey() {
        return CREATED_AT;
    }

    public static ExchangeSortKey fromString(String value) {
        if (value == null || value.isBlank()) { return CREATED_AT; }
        return valueOf(value);
    }
}
