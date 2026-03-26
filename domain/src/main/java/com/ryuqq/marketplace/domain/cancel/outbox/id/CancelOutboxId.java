package com.ryuqq.marketplace.domain.cancel.outbox.id;

/** 취소 아웃박스 ID Value Object. */
public record CancelOutboxId(Long value) {

    public static CancelOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CancelOutboxId 값은 null일 수 없습니다");
        }
        return new CancelOutboxId(value);
    }

    public static CancelOutboxId forNew() {
        return new CancelOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
