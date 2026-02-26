package com.ryuqq.marketplace.domain.commoncodetype.vo;

/** 공통 코드 타입 설명 Value Object. nullable. */
public record CommonCodeTypeDescription(String value) {

    private static final int MAX_LENGTH = 500;

    public CommonCodeTypeDescription {
        if (value != null) {
            value = value.trim();
            if (value.isBlank()) {
                value = null;
            } else if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("공통 코드 타입 설명은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
            }
        }
    }

    public static CommonCodeTypeDescription of(String value) {
        return new CommonCodeTypeDescription(value);
    }

    public static CommonCodeTypeDescription empty() {
        return new CommonCodeTypeDescription(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
