package com.ryuqq.marketplace.domain.inboundsource.id;

/** InboundSource ID Value Object. */
public record InboundSourceId(Long value) {

    public static InboundSourceId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InboundSourceId 값은 null일 수 없습니다");
        }
        return new InboundSourceId(value);
    }

    public static InboundSourceId forNew() {
        return new InboundSourceId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
