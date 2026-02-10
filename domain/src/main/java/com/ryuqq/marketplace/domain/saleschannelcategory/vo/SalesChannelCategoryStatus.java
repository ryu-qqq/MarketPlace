package com.ryuqq.marketplace.domain.saleschannelcategory.vo;

import java.util.Locale;

public enum SalesChannelCategoryStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static SalesChannelCategoryStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return SalesChannelCategoryStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
