package com.ryuqq.marketplace.domain.selleradmin.vo;

/**
 * 관리자 이름 Value Object.
 *
 * <p>셀러 관리자의 이름입니다.
 */
public record AdminName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;

    public AdminName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("관리자 이름은 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "관리자 이름은 " + MIN_LENGTH + "자 이상 " + MAX_LENGTH + "자 이하여야 합니다");
        }
    }

    public static AdminName of(String value) {
        return new AdminName(value);
    }
}
