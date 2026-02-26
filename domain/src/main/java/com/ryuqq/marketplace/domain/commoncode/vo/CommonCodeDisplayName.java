package com.ryuqq.marketplace.domain.commoncode.vo;

/**
 * 공통 코드 표시명 Value Object.
 *
 * <p>사용자에게 보여지는 공통 코드의 이름입니다.
 */
public record CommonCodeDisplayName(String value) {

    private static final int MAX_LENGTH = 100;

    public CommonCodeDisplayName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("공통 코드 표시명은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("공통 코드 표시명은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
    }

    public static CommonCodeDisplayName of(String value) {
        return new CommonCodeDisplayName(value);
    }
}
