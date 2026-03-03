package com.ryuqq.marketplace.adapter.out.persistence.order;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderHistoryJpaEntity;
import java.time.Instant;

/** OrderHistoryJpaEntity 테스트 Fixtures. */
public final class OrderHistoryJpaEntityFixtures {

    private OrderHistoryJpaEntityFixtures() {}

    public static final String DEFAULT_CHANGED_BY = "SYSTEM";

    /** 주문 생성 이력 Entity. */
    public static OrderHistoryJpaEntity creationHistory(String orderId) {
        Instant now = Instant.now();
        return OrderHistoryJpaEntity.create(
                null, orderId, null, "ORDERED", DEFAULT_CHANGED_BY, null, now, now, now);
    }

    /** 상태 전이 이력 Entity. */
    public static OrderHistoryJpaEntity transitionHistory(
            String orderId, String fromStatus, String toStatus) {
        Instant now = Instant.now();
        return OrderHistoryJpaEntity.create(
                null, orderId, fromStatus, toStatus, DEFAULT_CHANGED_BY, null, now, now, now);
    }

    /** 사유가 있는 상태 전이 이력 Entity. */
    public static OrderHistoryJpaEntity historyWithReason(
            String orderId, String fromStatus, String toStatus, String reason) {
        Instant now = Instant.now();
        return OrderHistoryJpaEntity.create(
                null, orderId, fromStatus, toStatus, DEFAULT_CHANGED_BY, reason, now, now, now);
    }
}
