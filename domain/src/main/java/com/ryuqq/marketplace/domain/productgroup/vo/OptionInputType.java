package com.ryuqq.marketplace.domain.productgroup.vo;

/** 옵션 그룹 입력 유형. */
public enum OptionInputType {
    PREDEFINED,
    FREE_INPUT;

    /** 모든 inputType에서 optionValues가 최소 1개 필수. */
    public boolean requiresOptionValues() {
        return true;
    }

    /** name이 null이면 PREDEFINED를 기본값으로 반환. */
    public static OptionInputType fromNameOrDefault(String name) {
        return name != null ? valueOf(name) : PREDEFINED;
    }
}
