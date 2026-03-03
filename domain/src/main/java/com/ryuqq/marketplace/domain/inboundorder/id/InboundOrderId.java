package com.ryuqq.marketplace.domain.inboundorder.id;

/** InboundOrder ID Value Object. */
public record InboundOrderId(Long value) {

    public static InboundOrderId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InboundOrderId 값은 null일 수 없습니다");
        }
        return new InboundOrderId(value);
    }

    public static InboundOrderId forNew() {
        return new InboundOrderId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
