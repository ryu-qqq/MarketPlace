package com.ryuqq.marketplace.domain.inboundcategorymapping.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** InboundCategoryMapping 정렬 키. */
public enum InboundCategoryMappingSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt");

    private final String fieldName;

    InboundCategoryMappingSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static InboundCategoryMappingSortKey defaultKey() {
        return CREATED_AT;
    }
}
