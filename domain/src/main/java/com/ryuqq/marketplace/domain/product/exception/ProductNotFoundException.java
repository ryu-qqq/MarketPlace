package com.ryuqq.marketplace.domain.product.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.List;
import java.util.Map;

/** 상품을 찾을 수 없을 때 발생하는 예외. */
public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(Long productId) {
        super(
                ProductErrorCode.PRODUCT_NOT_FOUND,
                String.format("상품을 찾을 수 없습니다: %d", productId),
                Map.of("productId", productId));
    }

    public ProductNotFoundException(List<Long> missingProductIds) {
        super(
                ProductErrorCode.PRODUCT_NOT_FOUND,
                String.format("상품을 찾을 수 없습니다: %s", missingProductIds),
                Map.of("productIds", missingProductIds));
    }
}
