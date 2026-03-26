package com.ryuqq.marketplace.application.shipment.dto.command;

/**
 * 단건 송장등록 커맨드.
 *
 * @param orderItemId 상품주문 ID
 * @param trackingNumber 송장번호
 * @param courierCode 택배사 코드
 * @param courierName 택배사명
 * @param shipmentMethodType 배송 방법 유형
 */
public record ShipSingleCommand(
        String orderItemId,
        String trackingNumber,
        String courierCode,
        String courierName,
        String shipmentMethodType) {}
