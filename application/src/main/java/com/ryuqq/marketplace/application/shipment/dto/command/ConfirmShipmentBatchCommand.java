package com.ryuqq.marketplace.application.shipment.dto.command;

import java.util.List;

/**
 * 발주확인 일괄 처리 커맨드.
 *
 * @param orderItemIds 발주확인 대상 상품주문 ID 목록
 * @param sellerId 셀러 ID (SUPER_ADMIN이면 null → 소유권 검증 skip)
 */
public record ConfirmShipmentBatchCommand(List<String> orderItemIds, Long sellerId) {

    public ConfirmShipmentBatchCommand {
        orderItemIds = orderItemIds != null ? List.copyOf(orderItemIds) : List.of();
    }
}
