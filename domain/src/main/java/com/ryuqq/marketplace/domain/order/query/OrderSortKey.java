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

    public static OrderSortKey fromString(String value) {
        if (value == null || value.isBlank()) { return defaultKey(); }
        for (OrderSortKey key : values()) {
            if (key.fieldName().equalsIgnoreCase(value) || key.name().equalsIgnoreCase(value)) { return key; }
        }
        return valueOf(value);
    }
}
