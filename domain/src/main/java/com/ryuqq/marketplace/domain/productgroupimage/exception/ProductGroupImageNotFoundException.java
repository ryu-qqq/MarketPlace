package com.ryuqq.marketplace.domain.productgroupimage.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupErrorCode;
import java.util.Map;

/** 상품 그룹 이미지를 찾을 수 없을 때 발생하는 예외. */
public class ProductGroupImageNotFoundException extends DomainException {

    public ProductGroupImageNotFoundException(Long imageId) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_IMAGE_NOT_FOUND,
                String.format("상품 그룹 이미지를 찾을 수 없습니다: %d", imageId),
                Map.of("imageId", imageId));
    }
}
