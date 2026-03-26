package com.ryuqq.marketplace.domain.refund.outbox.id;

/** 환불 아웃박스 ID Value Object. */
public record RefundOutboxId(Long value) {

    public static RefundOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("RefundOutboxId 값은 null일 수 없습니다");
        }
        return new RefundOutboxId(value);
    }

    public static RefundOutboxId forNew() {
        return new RefundOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
