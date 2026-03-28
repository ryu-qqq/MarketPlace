package com.ryuqq.marketplace.domain.order.id;

/** 주문 상품 ID Value Object. 외부에서 Long 값을 주입받습니다. */
public record OrderItemId(Long value) {

    public static OrderItemId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OrderItemId 값은 null일 수 없습니다");
        }
        return new OrderItemId(value);
    }

    public static OrderItemId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderItemId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return of(Long.parseLong(value));
    }

    public static OrderItemId forNew(Long value) {
        return of(value);
    }
}
