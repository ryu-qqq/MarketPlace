package com.ryuqq.marketplace.domain.cancel.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** 취소 검색 필드. */
public enum CancelSearchField implements SearchField {
    CANCEL_NUMBER("cancelNumber"),
    ORDER_NUMBER("orderNumber"),
    CUSTOMER_NAME("customerName"),
    CUSTOMER_PHONE("customerPhone"),
    PRODUCT_NAME("productName");

    private final String fieldName;

    CancelSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static CancelSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (CancelSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
