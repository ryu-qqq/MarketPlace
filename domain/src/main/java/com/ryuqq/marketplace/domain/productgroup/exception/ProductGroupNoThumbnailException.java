package com.ryuqq.marketplace.domain.productgroup.exception;

import java.util.Map;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 판매 활성화 시 THUMBNAIL 이미지가 없을 때 발생하는 예외. */
public class ProductGroupNoThumbnailException extends DomainException {

    public ProductGroupNoThumbnailException(Long productGroupId) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_NO_THUMBNAIL,
                String.format("상품 그룹(%d)에 대표 이미지(THUMBNAIL)가 없습니다", productGroupId),
                Map.of("productGroupId", productGroupId));
    }
}
