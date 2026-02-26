package com.ryuqq.marketplace.domain.legacy.productimage.vo;

/** 레거시(세토프) 상품 이미지 유형 enum. */
public enum ProductGroupImageType {
    MAIN("대표 이미지"),
    DETAIL("상세 이미지"),
    DESCRIPTION("상세 설명 이미지");

    private final String description;

    ProductGroupImageType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
