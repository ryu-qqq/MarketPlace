package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/** 레거시(세토프) 반품 방법 enum. */
public enum ReturnMethod {
    RETURN_SELLER("판매자 수거"),
    NOT_PROCEED_RETURN("진행안함"),
    RETURN_CONSUMER("구매자 직접 반송"),
    REFER_DETAIL("상세 정보 참고");

    private final String description;

    ReturnMethod(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
