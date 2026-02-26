package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/** 레거시(세토프) 상품 상태 enum. */
public enum ProductCondition {
    NEW("새상품"),
    USED("중고 상품");

    private final String description;

    ProductCondition(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
