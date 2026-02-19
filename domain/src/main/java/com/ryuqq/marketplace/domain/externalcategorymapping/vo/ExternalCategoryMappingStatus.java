package com.ryuqq.marketplace.domain.externalcategorymapping.vo;

import java.util.Locale;

/** 외부 카테고리 매핑 상태. */
public enum ExternalCategoryMappingStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static ExternalCategoryMappingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return ExternalCategoryMappingStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
