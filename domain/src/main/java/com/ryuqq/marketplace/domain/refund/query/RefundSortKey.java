package com.ryuqq.marketplace.domain.refund.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 환불 정렬 키. */
public enum RefundSortKey implements SortKey {
    REQUESTED_AT("requestedAt"),
    COMPLETED_AT("completedAt"),
    CREATED_AT("createdAt");

    private final String fieldName;

    RefundSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static RefundSortKey defaultKey() {
        return CREATED_AT;
    }
}
