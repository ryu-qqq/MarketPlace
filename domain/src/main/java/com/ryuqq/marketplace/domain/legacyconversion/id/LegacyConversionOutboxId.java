package com.ryuqq.marketplace.domain.legacyconversion.id;

/**
 * 레거시 변환 Outbox ID Value Object.
 *
 * <p>레거시 상품 → 내부 상품 변환 요청을 식별하는 ID입니다.
 */
public record LegacyConversionOutboxId(Long value) {

    public static LegacyConversionOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyConversionOutboxId 값은 null일 수 없습니다");
        }
        return new LegacyConversionOutboxId(value);
    }

    public static LegacyConversionOutboxId forNew() {
        return new LegacyConversionOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
