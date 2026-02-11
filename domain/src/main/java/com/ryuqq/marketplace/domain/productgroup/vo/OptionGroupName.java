package com.ryuqq.marketplace.domain.productgroup.vo;

/** 셀러 옵션 그룹명 Value Object. (예: "색상", "사이즈") */
public record OptionGroupName(String value) {

    private static final int MAX_LENGTH = 100;

    public OptionGroupName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("옵션 그룹명은 필수입니다");
        }
        value = value.trim();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("옵션 그룹명은 %d자 이내여야 합니다", MAX_LENGTH));
        }
    }

    public static OptionGroupName of(String value) {
        return new OptionGroupName(value);
    }
}
