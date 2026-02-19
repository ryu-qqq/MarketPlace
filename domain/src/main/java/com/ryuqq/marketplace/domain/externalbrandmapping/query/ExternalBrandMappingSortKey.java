package com.ryuqq.marketplace.domain.externalbrandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** ExternalBrandMapping 정렬 키. */
public enum ExternalBrandMappingSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt");

    private final String fieldName;

    ExternalBrandMappingSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ExternalBrandMappingSortKey defaultKey() {
        return CREATED_AT;
    }
}
