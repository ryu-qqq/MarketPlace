package com.ryuqq.marketplace.domain.order.id;

/** 주문 이력 ID Value Object. */
public record OrderHistoryId(Long value) {

    public static OrderHistoryId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OrderHistoryId 값은 null일 수 없습니다");
        }
        return new OrderHistoryId(value);
    }

    public static OrderHistoryId forNew() {
        return new OrderHistoryId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
