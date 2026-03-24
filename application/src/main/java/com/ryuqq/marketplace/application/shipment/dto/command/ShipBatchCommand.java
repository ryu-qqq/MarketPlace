package com.ryuqq.marketplace.application.shipment.dto.command;

import java.util.List;

/**
 * 송장등록 일괄 처리 커맨드.
 *
 * @param items 송장등록 대상 목록
 */
public record ShipBatchCommand(List<ShipBatchItem> items) {

    public ShipBatchCommand {
        items = items != null ? List.copyOf(items) : List.of();
    }

    /**
     * 송장등록 개별 항목.
     *
     * @param orderItemId 상품주문 ID (Service에서 orderItemNumber로 조회 후 세팅)
     * @param orderItemNumber 주문상품번호 (ORD-YYYYMMDD-XXXX-NNN)
     * @param trackingNumber 송장번호
     * @param courierCode 택배사 코드
     * @param shipmentMethodType 배송 방법 유형
     */
    public record ShipBatchItem(
            String orderItemId,
            String orderItemNumber,
            String trackingNumber,
            String courierCode,
            String shipmentMethodType) {}
}
