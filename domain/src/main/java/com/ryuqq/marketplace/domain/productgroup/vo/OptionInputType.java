package com.ryuqq.marketplace.domain.productgroup.vo;

/** 옵션 그룹 입력 유형. */
public enum OptionInputType {
    PREDEFINED,
    FREE_INPUT;

    /** 모든 inputType에서 optionValues가 최소 1개 필수. */
    public boolean requiresOptionValues() {
        return true;
    }
}
