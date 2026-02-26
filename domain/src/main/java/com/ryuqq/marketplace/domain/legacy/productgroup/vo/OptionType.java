package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/** 레거시(세토프) 옵션 유형 enum. */
public enum OptionType {
    OPTION_ONE("단일 옵션"),
    OPTION_TWO("복합 옵션"),
    SINGLE("단품");

    private final String description;

    OptionType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
