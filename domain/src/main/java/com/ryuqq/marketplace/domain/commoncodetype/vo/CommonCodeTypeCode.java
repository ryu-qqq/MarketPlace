package com.ryuqq.marketplace.domain.commoncodetype.vo;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 공통 코드 타입 코드 Value Object.
 *
 * <p>영문 대문자와 언더스코어만 허용합니다. (예: PAYMENT_METHOD, COURIER)
 */
public record CommonCodeTypeCode(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]*$");

    public CommonCodeTypeCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("공통 코드 타입 코드는 필수입니다");
        }
        value = value.trim().toUpperCase(Locale.ROOT);
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("공통 코드 타입 코드는 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("공통 코드 타입 코드는 영문 대문자로 시작하고, 영문 대문자/숫자/언더스코어만 허용됩니다");
        }
    }

    public static CommonCodeTypeCode of(String value) {
        return new CommonCodeTypeCode(value);
    }
}
