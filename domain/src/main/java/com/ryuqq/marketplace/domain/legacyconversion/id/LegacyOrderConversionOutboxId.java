package com.ryuqq.marketplace.domain.legacyconversion.id;

/** 레거시 주문 변환 Outbox ID Value Object. */
public record LegacyOrderConversionOutboxId(Long value) {

    public static LegacyOrderConversionOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyOrderConversionOutboxId 값은 null일 수 없습니다");
        }
        return new LegacyOrderConversionOutboxId(value);
    }

    public static LegacyOrderConversionOutboxId forNew() {
        return new LegacyOrderConversionOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
