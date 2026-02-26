package com.ryuqq.marketplace.domain.shop.vo;

import java.util.Locale;

/** 외부몰 상태. */
public enum ShopStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static ShopStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return ShopStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
