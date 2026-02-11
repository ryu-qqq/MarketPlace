package com.ryuqq.marketplace.domain.productgroup.exception;

import java.util.Map;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;

/** 유효하지 않은 상태 전이 시 발생하는 예외. */
public class ProductGroupInvalidStatusTransitionException extends DomainException {

    public ProductGroupInvalidStatusTransitionException(
            ProductGroupStatus currentStatus, ProductGroupStatus targetStatus) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_INVALID_STATUS_TRANSITION,
                String.format("%s 상태에서 %s 상태로 전환할 수 없습니다", currentStatus, targetStatus),
                Map.of("currentStatus", currentStatus.name(), "targetStatus", targetStatus.name()));
    }
}
