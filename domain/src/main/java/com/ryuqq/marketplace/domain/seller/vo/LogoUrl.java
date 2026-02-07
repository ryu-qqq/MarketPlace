package com.ryuqq.marketplace.domain.seller.vo;

import java.util.regex.Pattern;

/** 셀러 로고 URL Value Object. nullable. */
public record LogoUrl(String value) {

    private static final int MAX_LENGTH = 500;
    private static final Pattern URL_PATTERN =
            Pattern.compile("^https?://[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$");

    public LogoUrl {
        if (value != null) {
            value = value.trim();
            if (value.isBlank()) {
                value = null;
            } else {
                if (value.length() > MAX_LENGTH) {
                    throw new IllegalArgumentException("로고 URL은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
                }
                if (!URL_PATTERN.matcher(value).matches()) {
                    throw new IllegalArgumentException("유효하지 않은 URL 형식입니다: " + value);
                }
            }
        }
    }

    public static LogoUrl of(String value) {
        return new LogoUrl(value);
    }

    public static LogoUrl empty() {
        return new LogoUrl(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
