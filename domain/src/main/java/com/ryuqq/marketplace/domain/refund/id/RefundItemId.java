package com.ryuqq.marketplace.domain.refund.id;

/** 환불 대상 상품 ID. 영속화 전에는 null입니다. */
public record RefundItemId(Long value) {

    public static RefundItemId of(long value) {
        return new RefundItemId(value);
    }

    public static RefundItemId forNew() {
        return new RefundItemId(null);
    }
}
