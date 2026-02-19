package com.ryuqq.marketplace.domain.externalsource.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** ExternalSource 정렬 키. */
public enum ExternalSourceSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt");

    private final String fieldName;

    ExternalSourceSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ExternalSourceSortKey defaultKey() {
        return CREATED_AT;
    }
}
