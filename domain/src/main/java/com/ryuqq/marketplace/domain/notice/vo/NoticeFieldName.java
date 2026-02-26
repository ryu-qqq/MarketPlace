package com.ryuqq.marketplace.domain.notice.vo;

/** 고시정보 필드 이름 (표시명) Value Object. */
public record NoticeFieldName(String value) {

    private static final int MAX_LENGTH = 100;

    public NoticeFieldName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("고시정보 필드 이름은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("고시정보 필드 이름은 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static NoticeFieldName of(String value) {
        return new NoticeFieldName(value);
    }
}
