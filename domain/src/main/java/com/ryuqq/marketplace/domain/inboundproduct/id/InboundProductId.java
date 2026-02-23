package com.ryuqq.marketplace.domain.inboundproduct.id;

/** InboundProduct ID Value Object. */
public record InboundProductId(Long value) {

    public static InboundProductId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InboundProductId 값은 null일 수 없습니다");
        }
        return new InboundProductId(value);
    }

    public static InboundProductId forNew() {
        return new InboundProductId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
