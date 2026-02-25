package com.ryuqq.marketplace.domain.legacy.product.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 레거시 상품을 찾을 수 없을 때 발생하는 예외. */
public class LegacyProductNotFoundException extends DomainException {

    public LegacyProductNotFoundException(Long productId) {
        super(
                LegacyProductErrorCode.LEGACY_PRODUCT_NOT_FOUND,
                String.format("레거시 상품을 찾을 수 없습니다: %d", productId),
                Map.of("productId", productId));
    }
}
