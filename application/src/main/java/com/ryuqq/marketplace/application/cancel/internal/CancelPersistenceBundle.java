package com.ryuqq.marketplace.application.cancel.internal;

import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;

/**
 * 취소 퍼시스트 파라미터 객체.
 *
 * <p>Cancel + CancelOutbox + ClaimHistory + OrderItem + Shipment을 하나로 묶어 PersistenceFacade에 전달합니다.
 * 빈 목록은 List.of()로 전달하여 null-safe를 보장합니다.
 */
public record CancelPersistenceBundle(
        List<Cancel> cancels,
        List<CancelOutbox> outboxes,
        List<ClaimHistory> histories,
        List<OrderItem> orderItems,
        List<Shipment> shipments) {

    /** Cancel + Outbox + History만 저장 (거절 등). */
    public static CancelPersistenceBundle of(
            List<Cancel> cancels, List<CancelOutbox> outboxes, List<ClaimHistory> histories) {
        return new CancelPersistenceBundle(cancels, outboxes, histories, List.of(), List.of());
    }

    /** Cancel + Outbox + History + OrderItem + Shipment 전체 저장 (판매자 취소, 승인 등). */
    public static CancelPersistenceBundle withOrderItemsAndShipments(
            List<Cancel> cancels,
            List<CancelOutbox> outboxes,
            List<ClaimHistory> histories,
            List<OrderItem> orderItems,
            List<Shipment> shipments) {
        return new CancelPersistenceBundle(cancels, outboxes, histories, orderItems, shipments);
    }
}
