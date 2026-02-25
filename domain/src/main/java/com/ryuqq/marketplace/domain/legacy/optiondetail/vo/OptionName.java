package com.ryuqq.marketplace.domain.legacy.optiondetail.vo;

/** 레거시 옵션명 Value Object. */
public record OptionName(String value) {

    public static OptionName of(String value) {
        return new OptionName(value);
    }
}
