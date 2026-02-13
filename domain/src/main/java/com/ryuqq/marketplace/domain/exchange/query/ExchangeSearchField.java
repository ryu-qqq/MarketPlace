package com.ryuqq.marketplace.domain.exchange.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** 교환 검색 필드. */
public enum ExchangeSearchField implements SearchField {
    CLAIM_NUMBER("claimNumber"),
    ORDER_NUMBER("orderNumber"),
    CUSTOMER_NAME("customerName"),
    CUSTOMER_PHONE("customerPhone"),
    PRODUCT_NAME("productName");

    private final String fieldName;

    ExchangeSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static ExchangeSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (ExchangeSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
