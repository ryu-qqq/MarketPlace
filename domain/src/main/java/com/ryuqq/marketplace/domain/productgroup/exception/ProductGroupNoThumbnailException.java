package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** THUMBNAIL 이미지가 없거나 중복될 때 발생하는 예외. */
public class ProductGroupNoThumbnailException extends DomainException {

    public ProductGroupNoThumbnailException(Long productGroupId) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_NO_THUMBNAIL,
                String.format("상품 그룹(%d)에 대표 이미지(THUMBNAIL)가 없습니다", productGroupId),
                Map.of("productGroupId", productGroupId));
    }

    public ProductGroupNoThumbnailException(long thumbnailCount) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_NO_THUMBNAIL,
                String.format("THUMBNAIL 이미지가 정확히 1개 필요합니다 (현재 %d개)", thumbnailCount),
                Map.of("thumbnailCount", thumbnailCount));
    }
}
