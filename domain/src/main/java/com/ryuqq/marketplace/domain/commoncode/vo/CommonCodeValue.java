package com.ryuqq.marketplace.domain.commoncode.vo;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 공통 코드 값 Value Object.
 *
 * <p>공통 코드의 실제 코드값을 표현합니다. 영문 대문자와 숫자, 언더스코어만 허용됩니다.
 */
public record CommonCodeValue(String value) {

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]*$");
    private static final int MAX_LENGTH = 50;

    public CommonCodeValue {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("공통 코드 값은 필수입니다");
        }
        value = value.trim().toUpperCase(Locale.ROOT);
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("공통 코드 값은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("공통 코드 값은 영문 대문자로 시작하고, 영문 대문자/숫자/언더스코어만 허용됩니다");
        }
    }

    public static CommonCodeValue of(String value) {
        return new CommonCodeValue(value);
    }
}
