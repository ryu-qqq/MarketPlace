package com.ryuqq.marketplace.domain.imagevariant.id;

/**
 * 이미지 Variant ID Value Object.
 *
 * <p>변환된 이미지를 식별하는 ID입니다.
 */
public record ImageVariantId(Long value) {

    public static ImageVariantId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ImageVariantId 값은 null일 수 없습니다");
        }
        return new ImageVariantId(value);
    }

    public static ImageVariantId forNew() {
        return new ImageVariantId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
