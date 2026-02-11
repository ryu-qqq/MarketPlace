package com.ryuqq.marketplace.domain.product.exception;

import java.util.Map;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;

/** 유효하지 않은 상품 상태 전이 시 발생하는 예외. */
public class ProductInvalidStatusTransitionException extends DomainException {

    public ProductInvalidStatusTransitionException(ProductStatus currentStatus, ProductStatus targetStatus) {
        super(
                ProductErrorCode.PRODUCT_INVALID_STATUS_TRANSITION,
                String.format("상품 상태를 %s에서 %s로 변경할 수 없습니다", currentStatus, targetStatus),
                Map.of("currentStatus", currentStatus.name(),
                        "targetStatus", targetStatus.name()));
    }
}
