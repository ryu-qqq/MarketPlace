package com.ryuqq.marketplace.domain.imageupload.vo;

/**
 * 이미지 소스 타입.
 *
 * <p>Outbox가 어떤 종류의 이미지를 대상으로 하는지 구분합니다.
 */
public enum ImageSourceType {

    /** ProductGroup 이미지 (product_group_images 테이블). */
    PRODUCT_GROUP_IMAGE("상품그룹 이미지"),

    /** Description 이미지 (description_images 테이블). */
    DESCRIPTION_IMAGE("상세설명 이미지");

    private final String description;

    ImageSourceType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isProductGroupImage() {
        return this == PRODUCT_GROUP_IMAGE;
    }

    public boolean isDescriptionImage() {
        return this == DESCRIPTION_IMAGE;
    }
}
