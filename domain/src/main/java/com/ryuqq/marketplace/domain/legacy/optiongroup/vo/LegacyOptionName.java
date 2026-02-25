package com.ryuqq.marketplace.domain.legacy.optiongroup.vo;

/** 레거시(세토프) 옵션명 enum. */
public enum LegacyOptionName {
    SIZE("사이즈"),
    COLOR("색상"),
    DEFAULT_ONE("옵션1"),
    DEFAULT_TWO("옵션2");

    private final String description;

    LegacyOptionName(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
