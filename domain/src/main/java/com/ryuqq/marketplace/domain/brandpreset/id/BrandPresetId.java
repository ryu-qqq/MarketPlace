package com.ryuqq.marketplace.domain.brandpreset.id;

/** BrandPreset ID Value Object. */
public record BrandPresetId(Long value) {

    public static BrandPresetId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("BrandPresetId 값은 null일 수 없습니다");
        }
        return new BrandPresetId(value);
    }

    public static BrandPresetId forNew() {
        return new BrandPresetId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
