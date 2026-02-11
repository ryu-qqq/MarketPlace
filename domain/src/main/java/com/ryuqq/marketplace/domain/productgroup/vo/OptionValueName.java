package com.ryuqq.marketplace.domain.productgroup.vo;

/** 셀러 옵션 값 이름 Value Object. (예: "검정", "260", "XL") */
public record OptionValueName(String value) {

    private static final int MAX_LENGTH = 100;

    public OptionValueName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("옵션 값 이름은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("옵션 값 이름은 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static OptionValueName of(String value) {
        return new OptionValueName(value);
    }
}
