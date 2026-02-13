package com.ryuqq.marketplace.domain.imageupload.id;

/**
 * 이미지 업로드 Outbox ID Value Object.
 *
 * <p>S3 이미지 업로드 Outbox를 식별하는 ID입니다.
 */
public record ImageUploadOutboxId(Long value) {

    public static ImageUploadOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ImageUploadOutboxId 값은 null일 수 없습니다");
        }
        return new ImageUploadOutboxId(value);
    }

    public static ImageUploadOutboxId forNew() {
        return new ImageUploadOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
