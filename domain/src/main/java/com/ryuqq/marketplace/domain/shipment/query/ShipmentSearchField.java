package com.ryuqq.marketplace.domain.shipment.query;

import com.ryuqq.marketplace.domain.common.vo.SearchField;

/** 배송 검색 필드. */
public enum ShipmentSearchField implements SearchField {

    /** 주문 ID (프론트: ORDER_ID, 내부: orderItemId) */
    ORDER_ID("orderItemId"),

    /** 송장번호 */
    TRACKING_NUMBER("trackingNumber"),

    /** 고객명 */
    CUSTOMER_NAME("customerName"),

    /** 고객 전화번호 */
    CUSTOMER_PHONE("customerPhone"),

    /** 상품명 */
    PRODUCT_NAME("productName"),

    /** 외부몰 주문번호 */
    SHOP_ORDER_NO("shopOrderNo");

    private final String fieldName;

    ShipmentSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /** 문자열로부터 ShipmentSearchField 변환. */
    public static ShipmentSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (ShipmentSearchField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value) || field.name().equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }
}
