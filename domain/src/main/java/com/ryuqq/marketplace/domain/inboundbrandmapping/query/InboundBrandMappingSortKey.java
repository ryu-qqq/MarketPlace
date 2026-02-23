package com.ryuqq.marketplace.domain.inboundbrandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** InboundBrandMapping 정렬 키. */
public enum InboundBrandMappingSortKey implements SortKey {

    /** 등록일시 순 (기본값) */
    CREATED_AT("createdAt");

    private final String fieldName;

    InboundBrandMappingSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 기본 정렬 키 (등록일시) */
    public static InboundBrandMappingSortKey defaultKey() {
        return CREATED_AT;
    }
}
