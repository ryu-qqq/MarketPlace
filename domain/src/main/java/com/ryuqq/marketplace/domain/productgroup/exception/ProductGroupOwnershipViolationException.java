package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 셀러 소유가 아닌 상품 그룹에 접근할 때 발생하는 예외. */
public class ProductGroupOwnershipViolationException extends DomainException {

    public ProductGroupOwnershipViolationException(
            long sellerId, int requestedCount, int foundCount) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_OWNERSHIP_VIOLATION,
                String.format(
                        "셀러(%d) 소유가 아닌 상품 그룹이 포함되어 있습니다. 요청: %d건, 조회: %d건",
                        sellerId, requestedCount, foundCount),
                Map.of(
                        "sellerId",
                        sellerId,
                        "requestedCount",
                        requestedCount,
                        "foundCount",
                        foundCount));
    }
}
