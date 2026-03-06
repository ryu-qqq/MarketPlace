package com.ryuqq.marketplace.domain.inboundorder.id;

/** InboundOrderItem ID Value Object. */
public record InboundOrderItemId(Long value) {

    public static InboundOrderItemId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InboundOrderItemId 값은 null일 수 없습니다");
        }
        return new InboundOrderItemId(value);
    }

    public static InboundOrderItemId forNew() {
        return new InboundOrderItemId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
