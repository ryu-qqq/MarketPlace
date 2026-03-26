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

    public static OrderDateField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (OrderDateField field : values()) {
            if (field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return valueOf(value);
    }
}
