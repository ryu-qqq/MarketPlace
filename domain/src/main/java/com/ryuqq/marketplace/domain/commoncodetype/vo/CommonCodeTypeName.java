package com.ryuqq.marketplace.domain.commoncodetype.vo;

/** 공통 코드 타입 이름 Value Object. */
public record CommonCodeTypeName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public CommonCodeTypeName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("공통 코드 타입 이름은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("공통 코드 타입 이름은 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static CommonCodeTypeName of(String value) {
        return new CommonCodeTypeName(value);
    }
}
