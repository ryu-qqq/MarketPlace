package com.ryuqq.marketplace.domain.shipment.query;

/** 배송 날짜 검색 필드. */
public enum ShipmentDateField {

    /** 결제일 */
    PAYMENT,

    /** 발주확인일 */
    ORDER_CONFIRMED,

    /** 발송일 */
    SHIPPED;

    public static ShipmentDateField fromString(String value) {
        if (value == null || value.isBlank()) { return null; }
        for (ShipmentDateField field : values()) {
            if (field.name().equalsIgnoreCase(value)) { return field; }
        }
        return valueOf(value);
    }
}
