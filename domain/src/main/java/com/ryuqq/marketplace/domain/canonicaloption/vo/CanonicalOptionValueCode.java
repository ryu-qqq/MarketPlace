package com.ryuqq.marketplace.domain.canonicaloption.vo;

/** 캐노니컬 옵션 값 코드 Value Object. */
public record CanonicalOptionValueCode(String value) {

    private static final int MAX_LENGTH = 50;

    public CanonicalOptionValueCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("캐노니컬 옵션 값 코드는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("캐노니컬 옵션 값 코드는 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static CanonicalOptionValueCode of(String value) {
        return new CanonicalOptionValueCode(value);
    }
}
