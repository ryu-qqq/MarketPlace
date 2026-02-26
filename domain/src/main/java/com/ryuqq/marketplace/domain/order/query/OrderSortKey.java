package com.ryuqq.marketplace.domain.order.query;

import com.ryuqq.marketplace.domain.common.vo.SortKey;

/** 주문 정렬 키. */
public enum OrderSortKey implements SortKey {
    CREATED_AT("createdAt"),
    ORDERED_AT("orderedAt"),
    UPDATED_AT("updatedAt");

    private final String fieldName;

    OrderSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static OrderSortKey defaultKey() {
        return CREATED_AT;
    }
}
