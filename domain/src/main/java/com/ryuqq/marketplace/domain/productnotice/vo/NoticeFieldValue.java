package com.ryuqq.marketplace.domain.productnotice.vo;

/** 고시정보 필드 값 Value Object. */
public record NoticeFieldValue(String value) {

    public NoticeFieldValue {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("고시정보 값은 비어있을 수 없습니다");
        }
        if (value.length() > 500) {
            throw new IllegalArgumentException("고시정보 값은 500자를 초과할 수 없습니다");
        }
    }

    public static NoticeFieldValue of(String value) {
        return new NoticeFieldValue(value);
    }
}
