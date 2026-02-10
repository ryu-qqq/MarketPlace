package com.ryuqq.marketplace.domain.brandpreset.vo;

import java.util.Locale;

/** 브랜드 프리셋 상태. */
public enum BrandPresetStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static BrandPresetStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return BrandPresetStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
