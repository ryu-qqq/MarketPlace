package com.ryuqq.marketplace.domain.outboundproduct.id;

/** OutboundProduct ID Value Object. */
public record OutboundProductId(Long value) {

    public static OutboundProductId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OutboundProductId 값은 null일 수 없습니다");
        }
        return new OutboundProductId(value);
    }

    public static OutboundProductId forNew() {
        return new OutboundProductId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
