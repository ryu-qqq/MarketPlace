package com.ryuqq.marketplace.domain.brandmapping.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** BrandMapping 정렬 키. */
public enum BrandMappingSortKey implements SortKey {
    CREATED_AT("createdAt");

    private final String fieldName;

    BrandMappingSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static BrandMappingSortKey defaultKey() {
        return CREATED_AT;
    }
}
