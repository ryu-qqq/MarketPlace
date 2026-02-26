package com.ryuqq.marketplace.domain.refund.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** 환불 검색 필드. */
public enum RefundSearchField implements SearchField {
    CLAIM_NUMBER("claimNumber"),
    ORDER_NUMBER("orderNumber"),
    CUSTOMER_NAME("customerName"),
    CUSTOMER_PHONE("customerPhone"),
    PRODUCT_NAME("productName");

    private final String fieldName;

    RefundSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static RefundSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (RefundSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
