package com.ryuqq.marketplace.domain.imagetransform.id;

/**
 * 이미지 변환 Outbox ID Value Object.
 *
 * <p>이미지 변환 Outbox를 식별하는 ID입니다.
 */
public record ImageTransformOutboxId(Long value) {

    public static ImageTransformOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ImageTransformOutboxId 값은 null일 수 없습니다");
        }
        return new ImageTransformOutboxId(value);
    }

    public static ImageTransformOutboxId forNew() {
        return new ImageTransformOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
