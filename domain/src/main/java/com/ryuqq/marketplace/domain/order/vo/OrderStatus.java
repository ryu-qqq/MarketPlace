package com.ryuqq.marketplace.domain.order.vo;

import java.util.EnumSet;
import java.util.Set;

/** 주문 상태. */
public enum OrderStatus {
    ORDERED,
    PREPARING,
    SHIPPED,
    DELIVERED,
    CONFIRMED,
    CANCELLED,
    CLAIM_IN_PROGRESS,
    REFUNDED,
    EXCHANGED;

    private static final Set<OrderStatus> PREPARABLE = EnumSet.of(ORDERED);
    private static final Set<OrderStatus> SHIPPABLE = EnumSet.of(PREPARING);
    private static final Set<OrderStatus> DELIVERABLE = EnumSet.of(SHIPPED);
    private static final Set<OrderStatus> CONFIRMABLE = EnumSet.of(DELIVERED);
    private static final Set<OrderStatus> CANCELLABLE = EnumSet.of(ORDERED, PREPARING);
    private static final Set<OrderStatus> CLAIMABLE = EnumSet.of(DELIVERED, SHIPPED);
    private static final Set<OrderStatus> REFUNDABLE = EnumSet.of(CLAIM_IN_PROGRESS);
    private static final Set<OrderStatus> EXCHANGEABLE = EnumSet.of(CLAIM_IN_PROGRESS);

    public boolean canTransitionTo(OrderStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<OrderStatus> getAllowedFrom(OrderStatus target) {
        return switch (target) {
            case PREPARING -> PREPARABLE;
            case SHIPPED -> SHIPPABLE;
            case DELIVERED -> DELIVERABLE;
            case CONFIRMED -> CONFIRMABLE;
            case CANCELLED -> CANCELLABLE;
            case CLAIM_IN_PROGRESS -> CLAIMABLE;
            case REFUNDED -> REFUNDABLE;
            case EXCHANGED -> EXCHANGEABLE;
            default -> EnumSet.noneOf(OrderStatus.class);
        };
    }
}
