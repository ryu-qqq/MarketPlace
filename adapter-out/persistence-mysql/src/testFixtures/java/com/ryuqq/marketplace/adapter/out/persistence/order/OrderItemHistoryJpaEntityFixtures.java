package com.ryuqq.marketplace.adapter.out.persistence.order;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemHistoryJpaEntity;
import java.time.Instant;

/** OrderItemHistoryJpaEntity 테스트 Fixtures. */
public final class OrderItemHistoryJpaEntityFixtures {

    private OrderItemHistoryJpaEntityFixtures() {}

    public static final String DEFAULT_CHANGED_BY = "SYSTEM";

    /** 주문상품 생성 이력 Entity. */
    public static OrderItemHistoryJpaEntity creationHistory(String orderItemId) {
        Instant now = Instant.now();
        return OrderItemHistoryJpaEntity.create(
                null, orderItemId, null, "READY", DEFAULT_CHANGED_BY, null, now, now, now);
    }

    /** 상태 전이 이력 Entity. */
    public static OrderItemHistoryJpaEntity transitionHistory(
            String orderItemId, String fromStatus, String toStatus) {
        Instant now = Instant.now();
        return OrderItemHistoryJpaEntity.create(
                null, orderItemId, fromStatus, toStatus, DEFAULT_CHANGED_BY, null, now, now, now);
    }

    /** 사유가 있는 상태 전이 이력 Entity. */
    public static OrderItemHistoryJpaEntity historyWithReason(
            String orderItemId, String fromStatus, String toStatus, String reason) {
        Instant now = Instant.now();
        return OrderItemHistoryJpaEntity.create(
                null, orderItemId, fromStatus, toStatus, DEFAULT_CHANGED_BY, reason, now, now, now);
    }
}
