package com.ryuqq.marketplace.application.productgroupimage.dto.response;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.List;

/** 상품 그룹 이미지 조회 결과 DTO. */
public record ProductGroupImageResult(
        Long id,
        String originUrl,
        String uploadedUrl,
        String imageType,
        int sortOrder,
        List<ImageVariantResult> variants) {

    public ProductGroupImageResult {
        variants = variants != null ? List.copyOf(variants) : List.of();
    }

    public static ProductGroupImageResult from(ProductGroupImage image) {
        return new ProductGroupImageResult(
                image.idValue(),
                image.originUrlValue(),
                image.uploadedUrlValue(),
                image.imageType().name(),
                image.sortOrder(),
                List.of());
    }

    public ProductGroupImageResult withVariants(List<ImageVariantResult> variants) {
        return new ProductGroupImageResult(
                id, originUrl, uploadedUrl, imageType, sortOrder, variants);
    }
}
