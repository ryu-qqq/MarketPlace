package com.ryuqq.marketplace.domain.inboundbrandmapping.vo;

import java.util.Locale;

/** 외부 브랜드 매핑 상태. */
public enum InboundBrandMappingStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static InboundBrandMappingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return InboundBrandMappingStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
