package com.ryuqq.marketplace.domain.notice.vo;

/** 고시정보 필드 코드 Value Object. */
public record NoticeFieldCode(String value) {

    private static final int MAX_LENGTH = 50;

    public NoticeFieldCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("고시정보 필드 코드는 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("고시정보 필드 코드는 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static NoticeFieldCode of(String value) {
        return new NoticeFieldCode(value);
    }
}
