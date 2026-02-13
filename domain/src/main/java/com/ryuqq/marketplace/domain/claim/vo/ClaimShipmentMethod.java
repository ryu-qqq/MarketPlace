package com.ryuqq.marketplace.domain.claim.vo;

/** 클레임 배송 방식 정보. */
public record ClaimShipmentMethod(ShipmentMethodType type, String courierCode, String courierName) {

    public ClaimShipmentMethod {
        if (type == null) {
            throw new IllegalArgumentException("배송 방식은 필수입니다");
        }
        if (type != ShipmentMethodType.VISIT) {
            if (courierCode == null || courierCode.isBlank()) {
                throw new IllegalArgumentException("VISIT 이외의 배송 방식에서는 택배사 코드가 필수입니다");
            }
        }
    }

    public static ClaimShipmentMethod of(
            ShipmentMethodType type, String courierCode, String courierName) {
        return new ClaimShipmentMethod(type, courierCode, courierName);
    }

    public static ClaimShipmentMethod visit() {
        return new ClaimShipmentMethod(ShipmentMethodType.VISIT, null, null);
    }
}
