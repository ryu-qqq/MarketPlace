package com.ryuqq.marketplace.domain.categorypreset.vo;

import java.util.Locale;

/** 카테고리 프리셋 상태. */
public enum CategoryPresetStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static CategoryPresetStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return CategoryPresetStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
