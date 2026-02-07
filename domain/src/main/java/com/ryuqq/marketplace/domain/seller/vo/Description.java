package com.ryuqq.marketplace.domain.seller.vo;

/** 셀러 설명 Value Object. nullable. */
public record Description(String value) {

    private static final int MAX_LENGTH = 5000;

    public Description {
        if (value != null) {
            value = value.trim();
            if (value.isBlank()) {
                value = null;
            } else if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("설명은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
            }
        }
    }

    public static Description of(String value) {
        return new Description(value);
    }

    public static Description empty() {
        return new Description(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
