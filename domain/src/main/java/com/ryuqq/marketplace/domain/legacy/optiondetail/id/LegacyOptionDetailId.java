package com.ryuqq.marketplace.domain.legacy.optiondetail.id;

/** 레거시(세토프) 옵션 상세 ID Value Object. */
public record LegacyOptionDetailId(Long value) {

    public static LegacyOptionDetailId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyOptionDetailId 값은 null일 수 없습니다");
        }
        return new LegacyOptionDetailId(value);
    }

    public static LegacyOptionDetailId forNew() {
        return new LegacyOptionDetailId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
