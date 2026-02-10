package com.ryuqq.marketplace.domain.categorymapping.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** CategoryMapping 정렬 키. */
public enum CategoryMappingSortKey implements SortKey {
    CREATED_AT("createdAt");

    private final String fieldName;

    CategoryMappingSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static CategoryMappingSortKey defaultKey() {
        return CREATED_AT;
    }
}
