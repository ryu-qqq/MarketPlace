package com.ryuqq.marketplace.domain.order.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** 주문 검색 필드. */
public enum OrderSearchField implements SearchField {
    ORDER_ID("orderId"),
    ORDER_NUMBER("orderNumber"),
    CUSTOMER_NAME("customerName"),
    PRODUCT_NAME("productName");

    private final String fieldName;

    OrderSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static OrderSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (OrderSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
