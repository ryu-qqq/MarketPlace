package com.ryuqq.marketplace.domain.categorypreset.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** CategoryPreset 정렬 키. */
public enum CategoryPresetSortKey implements SortKey {
    CREATED_AT("createdAt");

    private final String fieldName;

    CategoryPresetSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static CategoryPresetSortKey defaultKey() {
        return CREATED_AT;
    }
}
