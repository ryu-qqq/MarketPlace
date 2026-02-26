package com.ryuqq.marketplace.domain.saleschannelcategory.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

public enum SalesChannelCategorySortKey implements SortKey {
    CREATED_AT("createdAt"),
    EXTERNAL_NAME("externalCategoryName"),
    SORT_ORDER("sortOrder");

    private final String fieldName;

    SalesChannelCategorySortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static SalesChannelCategorySortKey defaultKey() {
        return SORT_ORDER;
    }
}
