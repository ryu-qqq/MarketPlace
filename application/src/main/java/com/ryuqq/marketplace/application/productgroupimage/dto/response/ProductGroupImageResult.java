package com.ryuqq.marketplace.application.productgroupimage.dto.response;

import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;

/** 상품 그룹 이미지 조회 결과 DTO. */
public record ProductGroupImageResult(
        Long id, String originUrl, String uploadedUrl, String imageType, int sortOrder) {

    public static ProductGroupImageResult from(ProductGroupImage image) {
        return new ProductGroupImageResult(
                image.idValue(),
                image.originUrlValue(),
                image.uploadedUrlValue(),
                image.imageType().name(),
                image.sortOrder());
    }
}
