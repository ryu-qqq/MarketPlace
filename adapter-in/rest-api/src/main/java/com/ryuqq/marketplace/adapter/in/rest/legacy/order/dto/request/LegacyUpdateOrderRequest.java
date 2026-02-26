package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request;

/**
 * 세토프 UpdateOrder 호환 요청 DTO.
 *
 * <p>세토프 원본은 {@code @JsonTypeInfo} 기반 다형성 구조 (normalOrder, shipOrder, claimOrder,
 * claimRejectedAndShipmentOrder)이며, 이 DTO는 모든 필드를 플랫하게 수용한다. 구현 시 type 필드로 분기 처리한다.
 */
public record LegacyUpdateOrderRequest(
        String type,
        long orderId,
        String orderStatus,
        Boolean byPass,
        String changeReason,
        String changeDetailReason,
        LegacyShipmentInfoRequest shipmentInfo) {}
