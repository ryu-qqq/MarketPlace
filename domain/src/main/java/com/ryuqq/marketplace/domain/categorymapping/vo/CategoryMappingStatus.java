package com.ryuqq.marketplace.domain.categorymapping.vo;

import java.util.Locale;

/** 카테고리 매핑 상태. */
public enum CategoryMappingStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static CategoryMappingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return CategoryMappingStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
