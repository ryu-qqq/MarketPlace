package com.ryuqq.marketplace.domain.shop.vo;

/** 외부몰 계정 ID Value Object. */
public record AccountId(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;

    public AccountId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("계정 ID는 필수입니다");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("계정 ID는 %d~%d자 이내여야 합니다", MIN_LENGTH, MAX_LENGTH));
        }
    }

    public static AccountId of(String value) {
        return new AccountId(value);
    }
}
