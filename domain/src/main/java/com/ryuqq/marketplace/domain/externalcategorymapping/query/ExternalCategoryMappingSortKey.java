package com.ryuqq.marketplace.domain.externalcategorymapping.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** ExternalCategoryMapping 정렬 키. */
public enum ExternalCategoryMappingSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt");

    private final String fieldName;

    ExternalCategoryMappingSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static ExternalCategoryMappingSortKey defaultKey() {
        return CREATED_AT;
    }
}
