package com.ryuqq.marketplace.domain.productnotice.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Map;

/** 상품 고시정보를 찾을 수 없을 때 발생하는 예외. */
public class ProductNoticeNotFoundException extends DomainException {

    public ProductNoticeNotFoundException(Long productNoticeId) {
        super(
                ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND,
                String.format("상품 고시정보를 찾을 수 없습니다: %d", productNoticeId),
                Map.of("productNoticeId", productNoticeId));
    }

    public ProductNoticeNotFoundException(ProductGroupId productGroupId) {
        super(
                ProductNoticeErrorCode.PRODUCT_NOTICE_NOT_FOUND,
                String.format("상품 그룹(%d)의 고시정보를 찾을 수 없습니다", productGroupId.value()),
                Map.of("productGroupId", productGroupId.value()));
    }
}
