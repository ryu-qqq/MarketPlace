package com.ryuqq.marketplace.domain.categorypreset.id;

/** CategoryPreset ID Value Object. */
public record CategoryPresetId(Long value) {

    public static CategoryPresetId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CategoryPresetId 값은 null일 수 없습니다");
        }
        return new CategoryPresetId(value);
    }

    public static CategoryPresetId forNew() {
        return new CategoryPresetId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
