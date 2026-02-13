package com.ryuqq.marketplace.domain.order.query;

import com.ryuqq.marketplace.domain.common.vo.DateField;

/** 주문 날짜 필드. */
public enum OrderDateField implements DateField {
    ORDERED("orderedAt"),
    SHIPPED("shippedAt"),
    DELIVERED("deliveredAt");

    private final String fieldName;

    OrderDateField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static OrderDateField defaultField() {
        return ORDERED;
    }
}
