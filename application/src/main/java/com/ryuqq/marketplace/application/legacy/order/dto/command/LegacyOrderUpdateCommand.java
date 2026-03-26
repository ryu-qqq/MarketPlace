package com.ryuqq.marketplace.application.legacy.order.dto.command;

/**
 * 레거시 주문 상태 변경 커맨드.
 *
 * @param type 변경 유형 (normalOrder, shipOrder, claimOrder, claimRejectedAndShipmentOrder)
 * @param orderId 주문 ID
 * @param orderStatus 변경 대상 주문 상태
 * @param byPass 바이패스 여부
 * @param changeReason 변경 사유
 * @param changeDetailReason 변경 상세 사유
 * @param invoiceNo 송장번호 (shipOrder, claimRejectedAndShipmentOrder)
 * @param courierCode 배송업체 코드
 * @param shipmentType 배송 타입
 */
public record LegacyOrderUpdateCommand(
        String type,
        long orderId,
        String orderStatus,
        Boolean byPass,
        String changeReason,
        String changeDetailReason,
        String invoiceNo,
        String courierCode,
        String shipmentType) {}
