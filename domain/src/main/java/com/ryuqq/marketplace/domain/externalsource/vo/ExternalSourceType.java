package com.ryuqq.marketplace.domain.externalsource.vo;

import java.util.Locale;

/** 외부 소스 유형. */
public enum ExternalSourceType {
    CRAWLING,
    LEGACY,
    PARTNER;

    public static ExternalSourceType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExternalSourceType 값은 필수입니다");
        }
        try {
            return ExternalSourceType.valueOf(value.toUpperCase(Locale.ROOT).trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("유효하지 않은 ExternalSourceType: %s", value));
        }
    }
}
