package com.ryuqq.marketplace.domain.order.id;

/** 주문 ID Value Object. 외부에서 UUIDv7을 주입받습니다. */
public record OrderId(String value) {

    public static OrderId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new OrderId(value);
    }

    public static OrderId forNew(String value) {
        return of(value);
    }
}
