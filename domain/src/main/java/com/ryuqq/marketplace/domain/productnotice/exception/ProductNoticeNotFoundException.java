package com.ryuqq.marketplace.domain.productnotice.exception;

import java.util.Map;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 상품 고시정보를 찾을 수 없을 때 발생하는 예외. */
public class ProductNoticeNotFoundException extends DomainException {

    public ProductNoticeNotFoundException(Long productNoticeId) {
        super(
                ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND,
                String.format("상품 고시정보를 찾을 수 없습니다: %d", productNoticeId),
                Map.of("productNoticeId", productNoticeId));
    }
}
