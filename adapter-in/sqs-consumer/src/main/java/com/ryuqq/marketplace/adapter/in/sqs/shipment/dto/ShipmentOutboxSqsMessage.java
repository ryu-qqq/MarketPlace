package com.ryuqq.marketplace.adapter.in.sqs.shipment.dto;

import java.util.Objects;

/**
 * 배송 Outbox SQS 메시지 DTO.
 *
 * @param outboxId Outbox ID
 * @param orderItemId 주문상품 ID
 * @param outboxType Outbox 유형 (CONFIRM, SHIP, DELIVER, CANCEL)
 * @param claimDomain 도메인 구분
 */
public record ShipmentOutboxSqsMessage(
        Long outboxId, String orderItemId, String outboxType, String claimDomain) {

    public ShipmentOutboxSqsMessage {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        Objects.requireNonNull(orderItemId, "orderItemId must not be null");
        Objects.requireNonNull(outboxType, "outboxType must not be null");
    }
}
