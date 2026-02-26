package com.ryuqq.marketplace.domain.saleschannel.vo;

import java.util.Locale;

/** 판매채널 상태. */
public enum SalesChannelStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static SalesChannelStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }
        try {
            return SalesChannelStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
