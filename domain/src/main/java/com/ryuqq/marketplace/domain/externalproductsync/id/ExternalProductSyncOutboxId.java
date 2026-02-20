package com.ryuqq.marketplace.domain.externalproductsync.id;

/** 외부 상품 연동 Outbox ID Value Object. */
public record ExternalProductSyncOutboxId(Long value) {

    public static ExternalProductSyncOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExternalProductSyncOutboxId 값은 null일 수 없습니다");
        }
        return new ExternalProductSyncOutboxId(value);
    }

    public static ExternalProductSyncOutboxId forNew() {
        return new ExternalProductSyncOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
