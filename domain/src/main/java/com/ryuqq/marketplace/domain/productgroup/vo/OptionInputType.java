package com.ryuqq.marketplace.domain.productgroup.vo;

/** 옵션 그룹 입력 유형. */
public enum OptionInputType {
    PREDEFINED,
    FREE_INPUT;

    /** 사전 정의 옵션값이 필수인지 여부. */
    public boolean requiresOptionValues() {
        return this == PREDEFINED;
    }
}
