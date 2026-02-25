package com.ryuqq.marketplace.domain.inboundsource.vo;

import java.util.Locale;

/** 인바운드 소스 유형. */
public enum InboundSourceType {
    CRAWLING,
    LEGACY,
    PARTNER;

    public static InboundSourceType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("InboundSourceType 값은 필수입니다");
        }
        try {
            return InboundSourceType.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("유효하지 않은 InboundSourceType: %s", value));
        }
    }
}
