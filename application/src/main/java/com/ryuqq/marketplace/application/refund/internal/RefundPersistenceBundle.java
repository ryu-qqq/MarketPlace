package com.ryuqq.marketplace.application.refund.internal;

import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.util.List;

/**
 * 환불 퍼시스트 파라미터 객체.
 *
 * <p>RefundClaim + RefundOutbox + ClaimHistory + OrderItem을 하나로 묶어 PersistenceFacade에 전달합니다. 빈 목록은
 * List.of()로 전달하여 null-safe를 보장합니다.
 */
public record RefundPersistenceBundle(
        List<RefundClaim> claims,
        List<RefundOutbox> outboxes,
        List<ClaimHistory> histories,
        List<OrderItem> orderItems) {

    /** RefundClaim + Outbox + History만 저장 (승인/거절/수거/보류 등). */
    public static RefundPersistenceBundle of(
            List<RefundClaim> claims, List<RefundOutbox> outboxes, List<ClaimHistory> histories) {
        return new RefundPersistenceBundle(claims, outboxes, histories, List.of());
    }

    /** RefundClaim + Outbox + History + OrderItem 전체 저장 (환불 요청 시). */
    public static RefundPersistenceBundle withOrderItems(
            List<RefundClaim> claims,
            List<RefundOutbox> outboxes,
            List<ClaimHistory> histories,
            List<OrderItem> orderItems) {
        return new RefundPersistenceBundle(claims, outboxes, histories, orderItems);
    }
}
