package com.ryuqq.marketplace.domain.settlement.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** 정산 검색 필드. */
public enum SettlementSearchField implements SearchField {
    ORDER_ID("orderId"),
    ORDER_NUMBER("orderNumber"),
    PRODUCT_NAME("productName"),
    BUYER_NAME("buyerName"),
    PAYMENT_ID("paymentId"),
    PAYMENT_NUMBER("paymentNumber");

    private final String fieldName;

    SettlementSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    public static SettlementSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (SettlementSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
