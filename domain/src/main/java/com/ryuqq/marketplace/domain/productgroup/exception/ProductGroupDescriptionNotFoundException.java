package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Map;

/** 상품 그룹 상세설명을 찾을 수 없을 때 발생하는 예외. */
public class ProductGroupDescriptionNotFoundException extends DomainException {

    public ProductGroupDescriptionNotFoundException(ProductGroupId productGroupId) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_DESCRIPTION_NOT_FOUND,
                String.format("상품 그룹(%d)의 상세설명을 찾을 수 없습니다", productGroupId.value()),
                Map.of("productGroupId", productGroupId.value()));
    }
}
