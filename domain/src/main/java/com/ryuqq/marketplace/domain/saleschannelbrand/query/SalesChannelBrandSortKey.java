package com.ryuqq.marketplace.domain.saleschannelbrand.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

public enum SalesChannelBrandSortKey implements SortKey {
    CREATED_AT("createdAt"),
    EXTERNAL_NAME("externalBrandName");

    private final String fieldName;

    SalesChannelBrandSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static SalesChannelBrandSortKey defaultKey() {
        return CREATED_AT;
    }
}
