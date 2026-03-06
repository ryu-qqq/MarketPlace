package com.ryuqq.marketplace.domain.outboundproduct.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 연동 이력 정렬 키. */
public enum SyncHistorySortKey implements SortKey {
    CREATED_AT("createdAt");

    private final String fieldName;

    SyncHistorySortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static SyncHistorySortKey defaultKey() {
        return CREATED_AT;
    }
}
