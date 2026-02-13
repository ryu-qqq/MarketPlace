package com.ryuqq.marketplace.application.shipment.dto.response;

import java.time.Instant;

/**
 * 배송 상세 조회 결과.
 *
 * @param shipmentId 배송 ID
 * @param shipmentNumber 배송번호
 * @param orderId 주문 ID
 * @param orderNumber 주문번호
 * @param status 배송 상태
 * @param shipmentMethod 배송 방법 정보
 * @param trackingNumber 송장번호
 * @param orderConfirmedAt 발주확인일시
 * @param shippedAt 발송일시
 * @param deliveredAt 배송완료일시
 * @param createdAt 등록일시
 * @param updatedAt 수정일시
 */
public record ShipmentDetailResult(
        String shipmentId,
        String shipmentNumber,
        String orderId,
        String orderNumber,
        String status,
        ShipmentMethodResult shipmentMethod,
        String trackingNumber,
        Instant orderConfirmedAt,
        Instant shippedAt,
        Instant deliveredAt,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * 배송 방법 결과.
     *
     * @param type 배송 방법 유형
     * @param courierCode 택배사 코드
     * @param courierName 택배사명
     */
    public record ShipmentMethodResult(String type, String courierCode, String courierName) {}
}
