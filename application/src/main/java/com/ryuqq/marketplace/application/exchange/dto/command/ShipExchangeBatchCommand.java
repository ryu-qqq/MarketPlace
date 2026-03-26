package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 재배송 일괄 처리 명령. */
public record ShipExchangeBatchCommand(List<ShipItem> items, String processedBy, Long sellerId) {

    public record ShipItem(
            String exchangeClaimId,
            String linkedOrderId,
            String deliveryCompany,
            String trackingNumber) {}
}
