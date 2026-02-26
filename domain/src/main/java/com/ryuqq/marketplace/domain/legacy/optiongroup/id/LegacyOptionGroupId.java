package com.ryuqq.marketplace.domain.legacy.optiongroup.id;

/** 레거시(세토프) 옵션 그룹 ID Value Object. */
public record LegacyOptionGroupId(Long value) {

    public static LegacyOptionGroupId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("LegacyOptionGroupId 값은 null일 수 없습니다");
        }
        return new LegacyOptionGroupId(value);
    }

    public static LegacyOptionGroupId forNew() {
        return new LegacyOptionGroupId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
