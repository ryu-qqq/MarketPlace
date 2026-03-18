package com.ryuqq.marketplace.domain.ordermapping.id;

/** 외부 주문 아이템 매핑 ID Value Object. DB auto-increment 기반. */
public record ExternalOrderItemMappingId(long value) {

    public static ExternalOrderItemMappingId of(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("ExternalOrderItemMappingId 값은 0보다 커야 합니다");
        }
        return new ExternalOrderItemMappingId(value);
    }

    public static ExternalOrderItemMappingId forNew() {
        return new ExternalOrderItemMappingId(0L);
    }

    public boolean isNew() {
        return value == 0L;
    }
}
