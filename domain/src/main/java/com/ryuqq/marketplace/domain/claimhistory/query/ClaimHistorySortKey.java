package com.ryuqq.marketplace.domain.claimhistory.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 클레임 이력 정렬 키. */
public enum ClaimHistorySortKey implements SortKey {
    CREATED_AT("createdAt");

    private final String fieldName;

    ClaimHistorySortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static ClaimHistorySortKey defaultKey() {
        return CREATED_AT;
    }

    public static ClaimHistorySortKey fromString(String value) {
        if (value == null || value.isBlank()) {
            return CREATED_AT;
        }
        return valueOf(value);
    }
}
