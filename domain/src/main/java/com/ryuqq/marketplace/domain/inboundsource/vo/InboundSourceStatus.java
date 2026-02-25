package com.ryuqq.marketplace.domain.inboundsource.vo;

import java.util.Locale;

/** 인바운드 소스 상태. */
public enum InboundSourceStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static InboundSourceStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return InboundSourceStatus.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
