package com.ryuqq.marketplace.domain.order.vo;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** 주문 상품 처리 상태. */
public enum OrderItemStatus {
    READY,
    CONFIRMED,
    CANCELLED,
    RETURN_REQUESTED,
    RETURNED;

    private static final Set<OrderItemStatus> CONFIRMABLE = EnumSet.of(READY);
    private static final Set<OrderItemStatus> CANCELLABLE = EnumSet.of(READY, CONFIRMED);
    private static final Set<OrderItemStatus> RETURN_REQUESTABLE = EnumSet.of(CONFIRMED);
    private static final Set<OrderItemStatus> RETURNABLE = EnumSet.of(RETURN_REQUESTED);

    public boolean canTransitionTo(OrderItemStatus target) {
        return getAllowedFrom(target).contains(this);
    }

    private static Set<OrderItemStatus> getAllowedFrom(OrderItemStatus target) {
        return switch (target) {
            case CONFIRMED -> CONFIRMABLE;
            case CANCELLED -> CANCELLABLE;
            case RETURN_REQUESTED -> RETURN_REQUESTABLE;
            case RETURNED -> RETURNABLE;
            default -> EnumSet.noneOf(OrderItemStatus.class);
        };
    }

    public static List<OrderItemStatus> fromStringList(List<String> values) {
        if (values == null || values.isEmpty()) { return List.of(); }
        return values.stream().map(s -> valueOf(s.toUpperCase(Locale.ROOT))).toList();
    }
}
