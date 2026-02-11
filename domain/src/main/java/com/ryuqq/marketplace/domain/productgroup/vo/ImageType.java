package com.ryuqq.marketplace.domain.productgroup.vo;

/** 상품 그룹 이미지 유형. */
public enum ImageType {
    THUMBNAIL("대표 이미지"),
    DETAIL("상세 이미지");

    private final String displayName;

    ImageType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
