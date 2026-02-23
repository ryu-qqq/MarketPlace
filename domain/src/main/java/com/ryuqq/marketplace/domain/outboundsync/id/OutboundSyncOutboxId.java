package com.ryuqq.marketplace.domain.outboundsync.id;

/** 외부 상품 연동 Outbox ID Value Object. */
public record OutboundSyncOutboxId(Long value) {

    public static OutboundSyncOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OutboundSyncOutboxId 값은 null일 수 없습니다");
        }
        return new OutboundSyncOutboxId(value);
    }

    public static OutboundSyncOutboxId forNew() {
        return new OutboundSyncOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
