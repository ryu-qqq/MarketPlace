package com.ryuqq.marketplace.domain.cancel.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 취소 정렬 키. */
public enum CancelSortKey implements SortKey {
    REQUESTED_AT("requestedAt"),
    COMPLETED_AT("completedAt"),
    CREATED_AT("createdAt");

    private final String fieldName;

    CancelSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static CancelSortKey defaultKey() {
        return CREATED_AT;
    }

    public static CancelSortKey fromString(String value) {
        if (value == null || value.isBlank()) { return CREATED_AT; }
        return valueOf(value);
    }
}
