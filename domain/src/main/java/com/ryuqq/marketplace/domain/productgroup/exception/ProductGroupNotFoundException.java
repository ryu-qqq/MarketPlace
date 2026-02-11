package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 상품 그룹을 찾을 수 없을 때 발생하는 예외. */
public class ProductGroupNotFoundException extends DomainException {

    public ProductGroupNotFoundException(Long productGroupId) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_NOT_FOUND,
                String.format("상품 그룹을 찾을 수 없습니다: %d", productGroupId),
                Map.of("productGroupId", productGroupId));
    }
}
