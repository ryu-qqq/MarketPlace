package com.ryuqq.marketplace.domain.order.id;

/** 주문 상품 ID Value Object. */
public record OrderItemId(Long value) {

    public static OrderItemId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OrderItemId 값은 null일 수 없습니다");
        }
        return new OrderItemId(value);
    }

    public static OrderItemId forNew() {
        return new OrderItemId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
