package com.ryuqq.marketplace.domain.externalsource.vo;

import java.util.Locale;

/** 외부 소스 상태. */
public enum ExternalSourceStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static ExternalSourceStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return ExternalSourceStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
