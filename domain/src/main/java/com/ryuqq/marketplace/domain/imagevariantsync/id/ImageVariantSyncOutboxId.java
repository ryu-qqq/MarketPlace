package com.ryuqq.marketplace.domain.imagevariantsync.id;

/**
 * 이미지 Variant Sync Outbox ID Value Object.
 *
 * <p>이미지 Variant Sync Outbox를 식별하는 ID입니다.
 *
 * @param value Outbox ID 값 (신규 생성 시 null)
 */
public record ImageVariantSyncOutboxId(Long value) {

    public static ImageVariantSyncOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ImageVariantSyncOutboxId 값은 null일 수 없습니다");
        }
        return new ImageVariantSyncOutboxId(value);
    }

    public static ImageVariantSyncOutboxId forNew() {
        return new ImageVariantSyncOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
