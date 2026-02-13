package com.ryuqq.marketplace.domain.shipment.id;

/** 배송 ID Value Object. 외부에서 UUIDv7을 주입받습니다. */
public record ShipmentId(String value) {

    public static ShipmentId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ShipmentId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new ShipmentId(value);
    }

    public static ShipmentId forNew(String value) {
        return of(value);
    }
}
