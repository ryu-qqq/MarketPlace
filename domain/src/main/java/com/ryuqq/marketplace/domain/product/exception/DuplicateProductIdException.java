package com.ryuqq.marketplace.domain.product.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.List;
import java.util.Map;

/** 배치 요청에 중복된 상품 ID가 포함된 경우 발생하는 예외. */
public class DuplicateProductIdException extends DomainException {

    public DuplicateProductIdException(List<Long> duplicateProductIds) {
        super(
                ProductErrorCode.DUPLICATE_PRODUCT_ID,
                String.format("중복된 상품 ID가 포함되어 있습니다: %s", duplicateProductIds),
                Map.of("duplicateProductIds", duplicateProductIds));
    }
}
