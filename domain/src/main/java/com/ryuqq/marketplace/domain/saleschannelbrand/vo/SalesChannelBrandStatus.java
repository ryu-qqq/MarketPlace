package com.ryuqq.marketplace.domain.saleschannelbrand.vo;

import java.util.Locale;

public enum SalesChannelBrandStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static SalesChannelBrandStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return SalesChannelBrandStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
