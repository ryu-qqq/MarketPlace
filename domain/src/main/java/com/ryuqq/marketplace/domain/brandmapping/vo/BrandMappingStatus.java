package com.ryuqq.marketplace.domain.brandmapping.vo;

import java.util.Locale;

/** 브랜드 매핑 상태. */
public enum BrandMappingStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static BrandMappingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return BrandMappingStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
