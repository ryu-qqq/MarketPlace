package com.ryuqq.marketplace.domain.product.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 해당 상품 그룹에 속하지 않는 상품에 접근할 때 발생하는 예외. */
public class ProductOwnershipViolationException extends DomainException {

    public ProductOwnershipViolationException(
            long productGroupId, int requestedCount, int foundCount) {
        super(
                ProductErrorCode.PRODUCT_OWNERSHIP_VIOLATION,
                String.format(
                        "상품 그룹(%d)에 속하지 않는 상품이 포함되어 있습니다. 요청: %d건, 조회: %d건",
                        productGroupId, requestedCount, foundCount),
                Map.of(
                        "productGroupId", productGroupId,
                        "requestedCount", requestedCount,
                        "foundCount", foundCount));
    }
}
