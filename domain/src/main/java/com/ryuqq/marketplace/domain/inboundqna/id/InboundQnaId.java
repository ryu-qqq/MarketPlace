package com.ryuqq.marketplace.domain.inboundqna.id;

public record InboundQnaId(Long value) {

    public static InboundQnaId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InboundQnaId value must not be null");
        }
        return new InboundQnaId(value);
    }

    public static InboundQnaId forNew() {
        return new InboundQnaId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
