package com.ryuqq.marketplace.domain.inboundcategorymapping.vo;

import java.util.Locale;

/** 외부 카테고리 매핑 상태. */
public enum InboundCategoryMappingStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static InboundCategoryMappingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return InboundCategoryMappingStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
