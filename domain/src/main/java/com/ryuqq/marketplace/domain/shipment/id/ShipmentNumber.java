package com.ryuqq.marketplace.domain.shipment.id;

/** 배송 번호 Value Object. "SHP-YYYYMMDD-XXXX" 형태. */
public record ShipmentNumber(String value) {

    public static ShipmentNumber of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ShipmentNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new ShipmentNumber(value);
    }
}
