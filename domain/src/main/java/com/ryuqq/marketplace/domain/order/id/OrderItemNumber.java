package com.ryuqq.marketplace.domain.order.id;

/** 주문 상품 번호 Value Object. "ORD-YYYYMMDD-XXXX-NNN" 형식입니다. */
public record OrderItemNumber(String value) {

    public OrderItemNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderItemNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    public static OrderItemNumber of(String value) {
        return new OrderItemNumber(value);
    }

    /** OrderNumber 기반으로 순번을 붙여 생성합니다. */
    public static OrderItemNumber generate(OrderNumber orderNumber, int sequence) {
        return new OrderItemNumber(orderNumber.value() + "-" + String.format("%03d", sequence));
    }
}
