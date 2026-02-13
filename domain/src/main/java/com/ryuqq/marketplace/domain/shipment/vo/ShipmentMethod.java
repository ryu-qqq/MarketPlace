package com.ryuqq.marketplace.domain.shipment.vo;

/** 배송 방법 VO. */
public record ShipmentMethod(ShipmentMethodType type, String courierCode, String courierName) {

    public ShipmentMethod {
        if (type == null) {
            throw new IllegalArgumentException("배송 방법 유형은 null일 수 없습니다");
        }
    }

    public static ShipmentMethod of(
            ShipmentMethodType type, String courierCode, String courierName) {
        return new ShipmentMethod(type, courierCode, courierName);
    }
}
