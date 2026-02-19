package com.ryuqq.marketplace.domain.shipment.vo;

/**
 * ShipmentShipData - 송장등록에 필요한 데이터 번들.
 *
 * <p>송장번호와 배송 방법을 함께 담는 불변 VO입니다.
 *
 * <p>Application Factory에서 {@code UpdateContext<ShipmentId, ShipmentShipData>}로 사용됩니다.
 *
 * @param trackingNumber 송장번호
 * @param method 배송 방법
 * @author ryu-qqq
 * @since 1.1.0
 */
public record ShipmentShipData(String trackingNumber, ShipmentMethod method) {

    public ShipmentShipData {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("송장번호는 null 또는 빈 문자열일 수 없습니다");
        }
        if (method == null) {
            throw new IllegalArgumentException("배송 방법은 null일 수 없습니다");
        }
    }

    public static ShipmentShipData of(String trackingNumber, ShipmentMethod method) {
        return new ShipmentShipData(trackingNumber, method);
    }
}
