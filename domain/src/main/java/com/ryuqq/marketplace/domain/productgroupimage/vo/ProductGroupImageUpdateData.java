package com.ryuqq.marketplace.domain.productgroupimage.vo;

import java.time.Instant;

/**
 * ProductGroupImage 수정 데이터.
 *
 * <p>수정할 새 이미지 목록과 수정 시각을 불변으로 보관합니다.
 */
public class ProductGroupImageUpdateData {

    private final ProductGroupImages newImages;
    private final Instant updatedAt;

    private ProductGroupImageUpdateData(ProductGroupImages newImages, Instant updatedAt) {
        this.newImages = newImages;
        this.updatedAt = updatedAt;
    }

    public static ProductGroupImageUpdateData of(ProductGroupImages newImages, Instant updatedAt) {
        return new ProductGroupImageUpdateData(newImages, updatedAt);
    }

    public ProductGroupImages newImages() {
        return newImages;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
