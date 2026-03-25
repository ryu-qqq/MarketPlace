package com.ryuqq.marketplace.domain.shipment.vo;

/** 배송 방법 유형. */
public enum ShipmentMethodType {

    /** 택배 */
    COURIER,

    /** 퀵 배송 */
    QUICK,

    /** 방문 수령 */
    VISIT,

    /** 지정 택배사 */
    DESIGNATED_COURIER;

    public static ShipmentMethodType fromString(String value) {
        if (value == null || value.isBlank()) { return COURIER; }
        for (ShipmentMethodType type : values()) {
            if (type.name().equalsIgnoreCase(value)) { return type; }
        }
        return COURIER;
    }
}
