package com.ryuqq.marketplace.domain.externalbrandmapping.vo;

import java.util.Locale;

/** 외부 브랜드 매핑 상태. */
public enum ExternalBrandMappingStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static ExternalBrandMappingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return ExternalBrandMappingStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
