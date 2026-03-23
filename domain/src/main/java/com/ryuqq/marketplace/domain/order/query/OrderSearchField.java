package com.ryuqq.marketplace.domain.order.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** 주문 검색 필드. 프론트 searchField 값과 1:1 매핑. */
public enum OrderSearchField implements SearchField {

    /** 주문번호 (프론트: ORDER_ID, 내부: order_items.order_item_number EQ) */
    ORDER_ID("orderItemNumber"),

    /** 결제번호 (프론트: PAYMENT_ID, 내부: payments.payment_number EQ) */
    PAYMENT_ID("paymentNumber"),

    /** 상품번호 (프론트: PRODUCT_GROUP_ID, 내부: order_items.product_group_id EQ) */
    PRODUCT_GROUP_ID("productGroupId"),

    /** 구매자명 (프론트: BUYER_NAME, 내부: orders.buyer_name LIKE) */
    BUYER_NAME("buyerName");

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
