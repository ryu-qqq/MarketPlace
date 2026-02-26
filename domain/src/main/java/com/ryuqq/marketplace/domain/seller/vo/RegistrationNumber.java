package com.ryuqq.marketplace.domain.seller.vo;

import java.util.regex.Pattern;

/** 사업자등록번호 Value Object. 형식: XXX-XX-XXXXX (10자리 숫자) */
public record RegistrationNumber(String value) {

    private static final Pattern REGISTRATION_PATTERN = Pattern.compile("^\\d{3}-\\d{2}-\\d{5}$");
    private static final Pattern DIGITS_ONLY_PATTERN = Pattern.compile("^\\d{10}$");

    public RegistrationNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("사업자등록번호는 필수입니다");
        }
        value = value.trim();

        // 숫자만 입력된 경우 형식 변환
        if (DIGITS_ONLY_PATTERN.matcher(value).matches()) {
            value = value.substring(0, 3) + "-" + value.substring(3, 5) + "-" + value.substring(5);
        }

        if (!REGISTRATION_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("유효하지 않은 사업자등록번호 형식입니다: " + value);
        }
    }

    public static RegistrationNumber of(String value) {
        return new RegistrationNumber(value);
    }

    /** 하이픈이 제거된 숫자만 반환합니다. */
    public String digitsOnly() {
        return value.replaceAll("-", "");
    }
}
