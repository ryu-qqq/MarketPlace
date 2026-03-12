package com.ryuqq.marketplace.domain.outboundproductimage.id;

/** OutboundProductImage ID Value Object. */
public record OutboundProductImageId(Long value) {

    public static OutboundProductImageId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OutboundProductImageId 값은 null일 수 없습니다");
        }
        return new OutboundProductImageId(value);
    }

    public static OutboundProductImageId forNew() {
        return new OutboundProductImageId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
