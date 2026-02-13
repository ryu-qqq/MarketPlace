package com.ryuqq.marketplace.application.shipment.dto.response;

import java.time.Instant;

/**
 * 배송 목록 조회 결과.
 *
 * @param shipmentId 배송 ID
 * @param shipmentNumber 배송번호
 * @param orderId 주문 ID
 * @param orderNumber 주문번호
 * @param status 배송 상태
 * @param trackingNumber 송장번호
 * @param courierName 택배사명
 * @param shippedAt 발송일시
 * @param deliveredAt 배송완료일시
 * @param createdAt 등록일시
 */
public record ShipmentListResult(
        String shipmentId,
        String shipmentNumber,
        String orderId,
        String orderNumber,
        String status,
        String trackingNumber,
        String courierName,
        Instant shippedAt,
        Instant deliveredAt,
        Instant createdAt) {}
