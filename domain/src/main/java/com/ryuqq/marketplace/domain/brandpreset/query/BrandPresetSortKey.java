package com.ryuqq.marketplace.domain.brandpreset.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** BrandPreset 정렬 키. */
public enum BrandPresetSortKey implements SortKey {
    CREATED_AT("createdAt");

    private final String fieldName;

    BrandPresetSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static BrandPresetSortKey defaultKey() {
        return CREATED_AT;
    }
}
