package com.ryuqq.marketplace.application.exchange.internal;

import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.List;

/**
 * 교환 퍼시스트 파라미터 객체.
 *
 * <p>ExchangeClaim + ExchangeOutbox + ClaimHistory + OrderItem을 하나로 묶어 PersistenceFacade에 전달합니다. 빈
 * 목록은 List.of()로 전달하여 null-safe를 보장합니다.
 */
public record ExchangePersistenceBundle(
        List<ExchangeClaim> claims,
        List<ExchangeOutbox> outboxes,
        List<ClaimHistory> histories,
        List<OrderItem> orderItems) {

    /** ExchangeClaim + Outbox + History만 저장 (승인/수거/재배송/거절/보류 등). */
    public static ExchangePersistenceBundle of(
            List<ExchangeClaim> claims,
            List<ExchangeOutbox> outboxes,
            List<ClaimHistory> histories) {
        return new ExchangePersistenceBundle(claims, outboxes, histories, List.of());
    }

    /** ExchangeClaim + History만 저장 (Outbox 불필요한 경우). */
    public static ExchangePersistenceBundle withoutOutboxes(
            List<ExchangeClaim> claims, List<ClaimHistory> histories) {
        return new ExchangePersistenceBundle(claims, List.of(), histories, List.of());
    }

    /** ExchangeClaim + History + OrderItem 전체 저장 (교환 요청/완료 시). */
    public static ExchangePersistenceBundle withOrderItems(
            List<ExchangeClaim> claims, List<ClaimHistory> histories, List<OrderItem> orderItems) {
        return new ExchangePersistenceBundle(claims, List.of(), histories, orderItems);
    }
}
